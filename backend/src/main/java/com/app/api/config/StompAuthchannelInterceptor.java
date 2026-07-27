package com.app.api.config;

import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
 
import java.security.Principal;


@Component
public class StompAuthchannelInterceptor implements ChannelInterceptor {
    

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel){
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if(StompCommand.CONNECT.equals(accessor.getCommand())){
            Object userId = accessor.getSessionAttributes() != null ? accessor.getSessionAttributes().get("userId") : null;
            
            if(userId != null){
                final String resolvedUserId = userId.toString();
                accessor.setUser((Principal) () -> resolvedUserId);
            }
        }
        return message;
    }
}
