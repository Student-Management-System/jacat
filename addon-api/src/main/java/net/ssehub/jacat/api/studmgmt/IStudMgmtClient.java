package net.ssehub.jacat.api.studmgmt;

import net.ssehub.studentmgmt.backend_api.ApiException;
import net.ssehub.studentmgmt.backend_api.api.*;

public interface IStudMgmtClient {

    AssignmentsApi getAssignmentsApi() throws ApiException;

    AssessmentsApi getAssessmentsApi() throws ApiException;

    AssessmentAllocationApi getAssessmentAllocationApi() throws ApiException;

    AssignmentRegistrationApi getAssignmentRegistrationApi() throws ApiException;

    AdmissionStatusApi getAdmissionStatusApi() throws ApiException;

    CourseParticipantsApi getCourseParticipantsApi() throws ApiException;

    CourseConfigApi getCourseConfigApi() throws ApiException;

    CoursesApi getCourseApi() throws ApiException;

    GroupsApi getGroupsApi() throws ApiException;

    UsersApi getUsersApi() throws ApiException;
}
