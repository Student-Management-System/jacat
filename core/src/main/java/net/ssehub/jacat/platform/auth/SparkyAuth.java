package net.ssehub.jacat.platform.auth;

import net.ssehub.studentmgmt.sparkyservice_api.ApiClient;
import net.ssehub.studentmgmt.sparkyservice_api.ApiException;
import net.ssehub.studentmgmt.sparkyservice_api.api.AuthControllerApi;
import net.ssehub.studentmgmt.sparkyservice_api.model.AuthenticationInfoDto;
import net.ssehub.studentmgmt.sparkyservice_api.model.CredentialsDto;
import net.ssehub.studentmgmt.sparkyservice_api.model.TokenDto;

public class SparkyAuth {

    private final TokenDto token;

    public SparkyAuth(String basePath, String username, String password) throws ApiException {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(basePath);

        AuthControllerApi authClient = new AuthControllerApi(apiClient);
        CredentialsDto credentials = new CredentialsDto()
            .username(username)
            .password(password);
        AuthenticationInfoDto authenticate = authClient.authenticate(credentials);
        token = authenticate.getToken();
        System.out.println(token.getExpiration());
    }

    public String getToken() {
        return token.getToken();
    }
}
