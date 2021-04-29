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
    public boolean addOrUpdatePartialAssessments(String courseId,
                                         String assignmentName,
                                         Map<String, PartialAssessmentDto> partialAssessments) {
        try {
            AssessmentsApi assessmentsApi = this.studMgmtClient.getAssessmentsApi();

            for (String groupOrUsername : partialAssessments.keySet()) {

                Optional<AssessmentDto> assessmentOptional = findAssessment(courseId, assignmentName, groupOrUsername);
                if (assessmentOptional.isPresent()) {
                    AssessmentDto assessment = assessmentOptional.get();

                    assessmentsApi.setPartialAssessment(partialAssessments.get(groupOrUsername),
                        courseId,
                        assessment.getAssignmentId(),
                        assessment.getId()
                    );
                }
            }

        } catch (ApiException e) {
            log.error("Cannot update PartialAssessments for " + assignmentName, e);
            return false;
        }
        return true;
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
    public List<AssessmentDto> findAssessments(String courseId,
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
}
