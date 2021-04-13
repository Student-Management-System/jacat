package net.ssehub.jacat.platform.course.config;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class CourseConfig {
    private String course;
    private String protocol;
    private String language;
    private Set<EventListenerConfig> listeners;
}