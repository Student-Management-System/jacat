package net.ssehub.jacat.worker.result;

import lombok.extern.slf4j.Slf4j;
import net.ssehub.jacat.api.studmgmt.IStudMgmtFacade;
import net.ssehub.jacat.api.studmgmt.IStudMgmtClient;
import net.ssehub.jacat.api.studmgmt.PAUpdateStrategy;
import net.ssehub.studentmgmt.backend_api.ApiException;
import net.ssehub.studentmgmt.backend_api.api.AssessmentsApi;
import net.ssehub.studentmgmt.backend_api.api.AssignmentsApi;
import net.ssehub.studentmgmt.backend_api.model.AssessmentDto;
import net.ssehub.studentmgmt.backend_api.model.AssessmentUpdateDto;
import net.ssehub.studentmgmt.backend_api.model.AssignmentDto;
import net.ssehub.studentmgmt.backend_api.model.PartialAssessmentDto;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class StudMgmtFacade implements IStudMgmtFacade {

    private final IStudMgmtClient studMgmtClient;

    public StudMgmtFacade(IStudMgmtClient studMgmtClient) {
        this.studMgmtClient = studMgmtClient;
    }

    @Override
    public boolean updatePartialAssessments(String courseId,
                                            String assignmentName,
                                            Map<String, PartialAssessmentDto> partialAssessments) {
        return addPartialAssessments(courseId, assignmentName, partialAssessments, PAUpdateStrategy.UPDATE);
    }

    @Override
    public boolean addPartialAssessments(String courseId, String assignmentName,
                                         Map<String, PartialAssessmentDto> partialAssessments,
                                         PAUpdateStrategy paUpdateStrategy) {
        try {
            AssessmentsApi assessmentsApi = this.studMgmtClient.getAssessmentsApi();

            List<PartialAssessmentDto> newPAs = this.addAssignmentToPA(courseId, assignmentName, partialAssessments);

            for (PartialAssessmentDto newPA : newPAs) {
                AssessmentUpdateDto updateAssessment = new AssessmentUpdateDto();
                updateAssessment.setAddPartialAssessments(Collections.singletonList(newPA));

                if (paUpdateStrategy.equals(PAUpdateStrategy.UPDATE)) {
                    List<PartialAssessmentDto> deletePAs = this.getOldPAsWithSameTitle(courseId, assignmentName, newPA);
                    updateAssessment.setRemovePartialAssignments(deletePAs);
                }

                assessmentsApi.updateAssessment(updateAssessment,
                    courseId,
                    getAssignment(courseId, assignmentName).get().getId(),
                    newPA.getAssessmentId()
                );
            }

        } catch (ApiException e) {
            log.error("Cannot update PartialAssessments for " + assignmentName, e);
            return false;
        }
        return true;
    }

    private List<PartialAssessmentDto> getOldPAsWithSameTitle(String courseId,
                                                              String assignmentName,
                                                              PartialAssessmentDto newPA) {

        Optional<AssignmentDto> assignmentOptional = this.getAssignment(courseId, assignmentName);
        if (assignmentOptional.isEmpty()) {
            return Collections.emptyList();
        }
        AssignmentDto assignment = assignmentOptional.get();

        try {
            AssessmentsApi assessmentsApi = this.studMgmtClient.getAssessmentsApi();

            AssessmentDto assessment = assessmentsApi.getAssessmentById(
                courseId, assignment.getId(), newPA.getAssessmentId()
            );

            return assessment.getPartialAssessments().stream()
                .filter(fPA -> fPA.getTitle().equals(newPA.getTitle()))
                .collect(Collectors.toList());
        } catch (ApiException e) {
            log.error("Cannot gather Old PartialAssessments for " + assignmentName, e);
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<AssignmentDto> getAssignment(String courseId, String assignmentName) {
        try {
            AssignmentsApi assignmentsApi = this.studMgmtClient.getAssignmentsApi();
            return assignmentsApi.getAssignmentsOfCourse(courseId).stream()
                .filter(assignment -> assignment.getName().equalsIgnoreCase(assignmentName))
                .findFirst();
        } catch (ApiException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<AssessmentDto> findAssessment(String courseId,
                                                  String assignmentName,
                                                  String groupOrUsername) {
        return findAssessments(courseId, assignmentName).stream()
            .filter(assessment -> (assessment.getParticipant() != null
                && assessment.getParticipant().getUsername().equals(groupOrUsername))
                    || (assessment.getGroup() != null
                    && assessment.getGroup().getName().equals(groupOrUsername))).findFirst();
    }

    @Override
    public List<AssessmentDto> findAssessments (String courseId,
            String assignmentName) {

        try {
            AssessmentsApi assessmentsApi = this.studMgmtClient.getAssessmentsApi();
            Optional<AssignmentDto> assignmentOptional = getAssignment(courseId, assignmentName);
            if (assignmentOptional.isPresent()) {
                AssignmentDto assignment = assignmentOptional.get();
                return assessmentsApi.getAssessmentsForAssignment(
                    courseId,
                    assignment.getId(),
                    null, null, null, null, null, null, null);
            }

        } catch (ApiException e) {
            log.error("Could not find Assessments because the api is not available: ", e);
            return Collections.emptyList();
        }

        return Collections.emptyList();
    }

    private List<PartialAssessmentDto> addAssignmentToPA(String courseId,
                                                         String assignmentName,
                                                         Map<String, PartialAssessmentDto> partialAssessments) {
        return partialAssessments.keySet().stream().map(groupOrUsername -> {
            Optional<AssessmentDto> assessment = findAssessment(courseId, assignmentName, groupOrUsername);
            if (assessment.isPresent()) {
                PartialAssessmentDto pa = partialAssessments.get(groupOrUsername);
                pa.setAssessmentId(assessment.get().getId());
                return pa;
            }
            return null;
        }).collect(Collectors.toList());
    }
}
