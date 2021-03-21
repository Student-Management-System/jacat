package net.ssehub.jacat.worker;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RuntimeConfiguration {

    @Bean("workdir")
    public Path workdir(@Value("${workdir:}") Path workdir) {
        System.out.println("workdir = " + workdir);
        if (workdir == null) {
            workdir = Paths.get(".").toAbsolutePath().normalize();
        }
        return workdir;
    }
}
