package net.ssehub.jacat.api.studmgmt;

import net.ssehub.studentmgmt.backend_api.model.AssessmentDto;
import net.ssehub.studentmgmt.backend_api.model.AssignmentDto;
import net.ssehub.studentmgmt.backend_api.model.PartialAssessmentDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IStudMgmtFacade {

    boolean addOrUpdatePartialAssessments(String courseId,
                                     String assignmentName,
                                     Map<String, PartialAssessmentDto> partialAssessments);

    Optional<AssignmentDto> getAssignment(String courseId, String assignmentName);

    Optional<AssessmentDto> findAssessment(String courseId,
                                           String assignmentName,
                                           String groupOrUsername);

    List<AssessmentDto> findAssessments(String courseId,
                                        String assignmentName);
}
