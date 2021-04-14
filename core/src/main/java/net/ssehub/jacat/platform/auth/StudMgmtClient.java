package net.ssehub.jacat.platform.auth;

import net.ssehub.studentmgmt.backend_api.ApiClient;
import net.ssehub.studentmgmt.backend_api.ApiException;
import net.ssehub.studentmgmt.backend_api.api.AssignmentsApi;
import net.ssehub.studentmgmt.backend_api.api.AuthenticationApi;
import net.ssehub.studentmgmt.backend_api.model.AuthSystemCredentials;
import net.ssehub.studentmgmt.backend_api.model.AuthTokenDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StudMgmtClient {

    private final SparkyAuth sparkyAuth;
    private final String basePath;

    public StudMgmtClient(SparkyAuth sparkyAuth,
                          @Value("${student-mgmt.basePath}") String basePath) {
        this.sparkyAuth = sparkyAuth;
        this.basePath = basePath;
    }

    public AssignmentsApi getAssignmentsApi() throws ApiException {
        return new AssignmentsApi(getApiClient());
    }

    private ApiClient getApiClient() throws ApiException {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(basePath);

        AuthenticationApi auth = new AuthenticationApi(apiClient);
        AuthSystemCredentials token = new AuthSystemCredentials().token(this.sparkyAuth.getToken());
        AuthTokenDto authToken = auth.loginWithToken(token);
        apiClient.setAccessToken(authToken.getAccessToken());
        return apiClient;
    }

}
