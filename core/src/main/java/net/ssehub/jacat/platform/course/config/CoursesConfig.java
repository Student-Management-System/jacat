package net.ssehub.jacat.platform.course.config;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "", ignoreUnknownFields = true)
public class CoursesConfig {
    private Set<CourseConfig> courses;

    public Set<CourseConfig> getCourses() {
        return courses;
    }

    public void setCourses(Set<CourseConfig> courses) {
        Set<CourseConfig> nonNull = courses
            .stream()
            .filter(
                course ->
                    course.getCourse() != null &&
                    course.getLanguage() != null &&
                    course.getProtocol() != null
            )
            .collect(Collectors.toSet());

        this.courses = nonNull;
    }

    public Optional<CourseConfig> getCourse(String course) {
        return courses
            .stream()
            .filter(course1 -> course1.getCourse().equalsIgnoreCase(course))
            .findFirst();
    }

    @Override
    public String toString() {
        return "CoursesConfiguration{" + "courses=" + courses + '}';
    }


}
