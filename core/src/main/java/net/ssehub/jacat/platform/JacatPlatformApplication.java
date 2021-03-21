package net.ssehub.jacat.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "net.ssehub.jacat")
public class JacatPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(JacatPlatformApplication.class, args);
    }
}
