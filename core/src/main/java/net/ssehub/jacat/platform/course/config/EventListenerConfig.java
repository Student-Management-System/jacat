package net.ssehub.jacat.platform.course.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.ssehub.studentmgmt.backend_api.model.NotificationDto;

import java.util.Map;

@Data
@NoArgsConstructor
public class EventListenerConfig {
    private NotificationDto.EventEnum event;
    private Map<String, Object> payload;

    private String analysis;
    private Map<String, Object> defaultParams;

    public boolean isListening(NotificationDto notification) {
        boolean isListening = this.event.equals(notification.getEvent());

        if (isListening && this.payload != null && !this.payload.isEmpty()) {
            Map<String, Object> eventPayload = (Map<String, Object>) notification.getPayload();
            for (String key : this.payload.keySet()) {
                if (!eventPayload.containsKey(key) || !eventPayload.get(key).equals(this.payload.get(key))) {
                    isListening = false;
                    break;
                }
            }
        }

        return isListening;
    }
}
