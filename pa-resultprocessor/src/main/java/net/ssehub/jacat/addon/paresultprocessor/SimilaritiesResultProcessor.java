package net.ssehub.jacat.addon.paresultprocessor;

import net.ssehub.jacat.api.addon.data.DataProcessingRequest;
import net.ssehub.jacat.api.addon.result.AbstractResultProcessor;
import net.ssehub.jacat.api.addon.task.Task;
import net.ssehub.jacat.api.studmgmt.IStudMgmtClient;
import net.ssehub.studentmgmt.backend_api.ApiException;
import net.ssehub.studentmgmt.backend_api.api.AssessmentsApi;
import net.ssehub.studentmgmt.backend_api.api.AssignmentsApi;
import net.ssehub.studentmgmt.backend_api.model.AssessmentDto;
import net.ssehub.studentmgmt.backend_api.model.AssessmentUpdateDto;
import net.ssehub.studentmgmt.backend_api.model.AssignmentDto;
import net.ssehub.studentmgmt.backend_api.model.PartialAssessmentDto;

import java.util.*;
import java.util.stream.Collectors;

public class SimilaritiesResultProcessor extends AbstractResultProcessor {

    public static final String PARTIAL_ASSESSMENT_TITLE = "PlagiarismCheck";
    private final IStudMgmtClient studMgmtClient;

    public SimilaritiesResultProcessor(IStudMgmtClient studMgmtClient) {
        this.studMgmtClient = studMgmtClient;
    }

    @Override
    public void process(Task task) {
        Map<String, Object> result = task.getResult();
        DataProcessingRequest taskConfig = task.getDataProcessingRequest();

        if (result == null
            || !result.containsKey("similarities")
            || !(result.get("similarities") instanceof List)) {
            return;
        }

        List<Similarity> similarities = ((List<Object>) result.get("similarities")).stream()
            .map(Similarity::fromResult)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        try {
            AssignmentsApi assignmentsApi = this.studMgmtClient.getAssignmentsApi();
            AssessmentsApi assessmentsApi = this.studMgmtClient.getAssessmentsApi();

            List<AssignmentDto> assignments = assignmentsApi.getAssignmentsOfCourse(taskConfig.getCourse());
            Optional<AssignmentDto> assignmentOptional = assignments.stream()
                .filter(fAssignment -> fAssignment.getName().equalsIgnoreCase(taskConfig.getHomework()))
                .findFirst();

            if (assignmentOptional.isEmpty()) {
                return;
            }

            AssignmentDto assignment = assignmentOptional.get();

            Map<String, AssessmentDto> assessments = assessmentsApi.getAssessmentsForAssignment(
                taskConfig.getCourse(),
                assignment.getId(),
                null, null, null, null, null, null, null)
                .stream()
                .collect(Collectors.toMap(assessment -> {
                    if (assessment.getGroup() != null) {
                        return assessment.getGroup().getName();
                    } else if (assessment.getParticipant() != null) {
                        return assessment.getParticipant().getUsername();
                    }
                    return null;
                }, assessment -> assessment));

            for (Map.Entry<String, AssessmentDto> assessmentEntry : assessments.entrySet()) {
                String groupOrUsername = assessmentEntry.getKey();
                AssessmentDto assessment = assessmentEntry.getValue();


                List<Similarity> groupOrUserSims = similarities.stream().filter(similarity -> {
                    boolean contains = similarity.getFrom().equalsIgnoreCase(groupOrUsername);

                    if (!contains) {
                        contains = similarity.getTo().stream()
                            .anyMatch(to -> to.getSubmission().equalsIgnoreCase(groupOrUsername));
                    }
                    return contains;
                }).collect(Collectors.toList());

                if (!groupOrUserSims.isEmpty()) {
                    PartialAssessmentDto partialAssessment = new PartialAssessmentDto();
                    partialAssessment.setTitle(PARTIAL_ASSESSMENT_TITLE);
                    partialAssessment.setAssessmentId(assessment.getId());
                    partialAssessment.setSeverity(PartialAssessmentDto.SeverityEnum.WARNING);

                    String toCombined = groupOrUserSims.stream()
                        .map(similarity -> {
                            String to = similarity.getFrom();
                            if (to.equalsIgnoreCase(groupOrUsername)) {
                                to = similarity.getTo()
                                    .stream()
                                    .map(subSim -> subSim.getSubmission() + " (" + subSim.getSimilarity() + "%)")
                                    .collect(Collectors.joining(", "));
                            } else {
                                Optional<Similarity.To> toFound = similarity.getTo().stream()
                                    .filter(toSim -> toSim.getSubmission().equalsIgnoreCase(groupOrUsername))
                                    .findFirst();
                                if (toFound.isPresent()) {
                                    to += " (" + toFound.get().getSimilarity() + "%)";
                                }
                            }

                            return to;
                        })
                        .collect(Collectors.joining(", "));
                    String comment = "Submission " + groupOrUsername + " might be similar to " + toCombined;
                    partialAssessment.setComment(comment);

                    List<PartialAssessmentDto> removeOldPAs = assessment.getPartialAssessments().stream()
                        .filter(pa -> pa.getTitle().equals(PARTIAL_ASSESSMENT_TITLE))
                        .collect(Collectors.toList());

                    AssessmentUpdateDto updateAssessment = new AssessmentUpdateDto();
                    updateAssessment.setAddPartialAssessments(Collections.singletonList(partialAssessment));
                    updateAssessment.setRemovePartialAssignments(removeOldPAs);

                    assessmentsApi.updateAssessment(updateAssessment,
                        taskConfig.getCourse(),
                        assignment.getId(),
                        assessment.getId()
                    );
                }
            }
        } catch (ApiException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
