package net.ssehub.jacat.platform.course;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import net.ssehub.jacat.platform.analysis.api.ListAnalysisResultDto;
import net.ssehub.jacat.platform.course.config.CoursesConfig;
import net.ssehub.studentmgmt.backend_api.model.NotificationDto;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v0/courses/notification")
public class NotificationsController {

    private final CoursesConfig coursesConfig;
    private final EventProcessor eventProcessor;

    public NotificationsController(CoursesConfig coursesConfig,
                                   EventProcessor eventProcessor) {
        this.coursesConfig = coursesConfig;
        this.eventProcessor = eventProcessor;
    }

    @Operation(summary = "Process a student management system notification")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notification processed",
            content = @Content)
    })
    @PostMapping
    public void receiveNotification(@RequestBody NotificationDto notification, HttpServletRequest request) {
        System.out.println(request.getRemoteAddr());
        System.out.println(request.getRemoteUser());
        System.out.println(request.getRemoteHost());

        coursesConfig.getCourse(notification.getCourseId()).ifPresent(
            course -> course.getListeners().stream()
                .filter(listener -> listener.isListening(notification))
                .forEach(listener ->
                    eventProcessor.process(notification.getAssignmentId(), course, listener)));
    }

}
