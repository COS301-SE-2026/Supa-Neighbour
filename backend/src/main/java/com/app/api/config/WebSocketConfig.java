package com.app.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

/**
 * Enables STOMP-over-WebSocket messaging.
 *
 * Endpoint: /ws (with SockJS fallback for clients/networks that block raw WS)
 * Broker prefixes:
 *   /topic  -> broadcast destinations (e.g. bulletin board zone feeds)
 *   /queue  -> per-user destinations (used with convertAndSendToUser)
 * App prefix: /app -> messages FROM client TO server (e.g. @MessageMapping methods)
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{
    private final StompHandshakeInterceptor stompHandshakeInterceptor;
    private final StompAuthchannelInterceptor stompAuthchannelInterceptor;

    @Autowired
    public WebSocketConfig(StompAuthchannelInterceptor stompAuthchannelInterceptor,StompHandshakeInterceptor stompHandshakeInterceptor){
        this.stompHandshakeInterceptor = stompHandshakeInterceptor;
        this.stompAuthchannelInterceptor = stompAuthchannelInterceptor;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*")
        .addInterceptors(stompHandshakeInterceptor).withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(org.springframework.messaging.simp.config.ChannelRegistration registration){
        registration.interceptors(stompAuthchannelInterceptor);
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration){
        registration.setMessageSizeLimit(128 * 1024);
        registration.setSendTimeLimit(10* 1000);
        registration.setSendBufferSizeLimit(512 * 1024);
    }
}