package net.ssehub.jacat.platform.course;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@ConfigurationProperties(prefix = "", ignoreUnknownFields = true)
public class CoursesConfiguration {

    private Set<Course> courses;

    public Set<Course> getCourses() {
        return courses;
    }

    public void setCourses(Set<Course> courses) {
        Set<Course> nonNull = courses.stream()
                .filter(course -> course.getCourse() != null
                    && course.getLanguage() != null
                    && course.getProtocol() != null)
                .collect(Collectors.toSet());

        this.courses = nonNull;
    }

    public Optional<Course> getCourse(String course) {
        return courses.stream()
                .filter(course1 -> course1.getCourse().equalsIgnoreCase(course))
                .findFirst();
    }

    @Override
    public String toString() {
        return "CoursesConfiguration{" +
                "courses=" + courses +
                '}';
    }

    public static class Course {

        private String course;
        private String protocol;
        private String language;

        public Course() {
        }

        public String getCourse() {
            return course;
        }

        public void setCourse(String course) {
            this.course = course;
        }

        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Course course1 = (Course) o;
            return course.equalsIgnoreCase(course1.course);
        }

        @Override
        public int hashCode() {
            return Objects.hash(course.toLowerCase());
        }

        @Override
        public String toString() {
            return "CourseConfiguration{" +
                    "course='" + course + '\'' +
                    ", protocol='" + protocol + '\'' +
                    '}';
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }
    }
}
