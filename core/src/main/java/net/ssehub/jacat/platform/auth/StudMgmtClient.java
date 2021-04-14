package net.ssehub.jacat.platform.auth;

import net.ssehub.jacat.api.studmgmt.IStudMgmtClient;
import net.ssehub.studentmgmt.backend_api.ApiClient;
import net.ssehub.studentmgmt.backend_api.ApiException;
import net.ssehub.studentmgmt.backend_api.api.*;
import net.ssehub.studentmgmt.backend_api.model.AuthSystemCredentials;
import net.ssehub.studentmgmt.backend_api.model.AuthTokenDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StudMgmtClient implements IStudMgmtClient {

    private final SparkyAuth sparkyAuth;
    private final String basePath;

    public StudMgmtClient(SparkyAuth sparkyAuth,
                          @Value("${student-mgmt.basePath}") String basePath) {
        this.sparkyAuth = sparkyAuth;
        this.basePath = basePath;
    }

    @Override
    public AssignmentsApi getAssignmentsApi() throws ApiException {
        return new AssignmentsApi(connect());
    }

    @Override
    public AssessmentsApi getAssessmentsApi() throws ApiException {
        return new AssessmentsApi(connect());
    }

    @Override
    public AssessmentAllocationApi getAssessmentAllocationApi() throws ApiException {
        return new AssessmentAllocationApi(connect());
    }

    @Override
    public AssignmentRegistrationApi getAssignmentRegistrationApi() throws ApiException {
        return new AssignmentRegistrationApi(connect());
    }

    @Override
    public AdmissionStatusApi getAdmissionStatusApi() throws ApiException {
        return new AdmissionStatusApi(connect());
    }

    @Override
    public CourseParticipantsApi getCourseParticipantsApi() throws ApiException {
        return new CourseParticipantsApi(connect());
    }

    @Override
    public CourseConfigApi getCourseConfigApi() throws ApiException {
        return new CourseConfigApi(connect());
    }

    @Override
    public CoursesApi getCourseApi() throws ApiException {
        return new CoursesApi(connect());
    }

    @Override
    public GroupsApi getGroupsApi() throws ApiException {
        return new GroupsApi(connect());
    }

    @Override
    public UsersApi getUsersApi() throws ApiException {
        return new UsersApi(connect());
    }

    private ApiClient connect() throws ApiException {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(basePath);

        AuthenticationApi auth = new AuthenticationApi(apiClient);
        AuthSystemCredentials token =
            new AuthSystemCredentials().token(this.sparkyAuth.getToken());
        AuthTokenDto authToken = auth.loginWithToken(token);
        apiClient.setAccessToken(authToken.getAccessToken());
        return apiClient;
    }

}
