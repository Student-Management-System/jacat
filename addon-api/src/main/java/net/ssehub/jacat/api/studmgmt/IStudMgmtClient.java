package net.ssehub.jacat.api.studmgmt;

import net.ssehub.studentmgmt.backend_api.ApiException;
import net.ssehub.studentmgmt.backend_api.api.*;

public interface IStudMgmtClient {

    AssignmentApi getAssignmentsApi() throws ApiException;

    AssessmentApi getAssessmentsApi() throws ApiException;

    AssessmentAllocationApi getAssessmentAllocationApi() throws ApiException;

    AssignmentRegistrationApi getAssignmentRegistrationApi() throws ApiException;

    AdmissionStatusApi getAdmissionStatusApi() throws ApiException;

    CourseParticipantsApi getCourseParticipantsApi() throws ApiException;

    CourseConfigApi getCourseConfigApi() throws ApiException;

    CourseApi getCourseApi() throws ApiException;

    GroupApi getGroupsApi() throws ApiException;

    UserApi getUsersApi() throws ApiException;
}
