package net.ssehub.jacat.platform.auth;

import net.ssehub.studentmgmt.sparkyservice_api.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkyAuthConfiguration {

    @Bean
    public SparkyAuth sparkyAuth(
        @Value("${sparky-auth.basePath}") String basePath,
        @Value("${sparky-auth.username}") String username,
        @Value("${sparky-auth.password}") String password) throws ApiException {
        return new SparkyAuth(basePath, username, password);
    }

}
