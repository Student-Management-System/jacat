package net.ssehub.jacat.platform.course;

import net.ssehub.jacat.platform.course.config.CourseConfig;
import net.ssehub.jacat.platform.course.config.CoursesConfig;
import net.ssehub.studentmgmt.backend_api.model.NotificationDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v0/courses/notifications")
public class NotificationsController {

    private final CoursesConfig coursesConfig;
    private final EventProcessor eventProcessor;

    public NotificationsController(CoursesConfig coursesConfig,
                                   EventProcessor eventProcessor) {
        this.coursesConfig = coursesConfig;
        this.eventProcessor = eventProcessor;
    }

    @PostMapping
    public void receiveNotification(@RequestBody NotificationDto notification) {
        coursesConfig.getCourse(notification.getCourseId()).ifPresent(
            course -> course.getListeners().stream()
                .filter(listener -> listener.isListening(notification))
                .forEach(listener -> eventProcessor.process(course, listener)));
    }

}
