package com.app.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class NotificationService {
    
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public NotificationService(SimpMessagingTemplate messagingTemplate){
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Sends to a single user's private queue. Client subscribes to:
     *   /user/queue/notifications
     * (Spring rewrites this under the hood to something like
     *  /queue/notifications-user<sessionId>, keyed off the Principal
     *  attached in StompAuthChannelInterceptor.)
     */
    public void notifyUser(String userId, String type, Map<String, Object> data){
        Map<String, Object> payload = Map.of(
            "type", type,
            "data", data
        );

        messagingTemplate.convertAndSendToUser(userId, "/queue/notifications", payload);
    }

    public void notifyUser(int userId, String type, Map<String, Object> data){
        notifyUser(String.valueOf(userId), type, data);
    }


    /**
     * Broadcasts to everyone subscribed to a zone's bulletin feed. Client
     * subscribes to something like:
     *   /topic/bulletin/{zoneId}
     */
    public void broadcastToZone(String zoneId, String type, Map<String, Object> data){
        Map<String, Object> payload = Map.of(
            "type", type,
            "data", data
        );

        messagingTemplate.convertAndSend("/topic/bulletin" + zoneId, payload);
    }
}
