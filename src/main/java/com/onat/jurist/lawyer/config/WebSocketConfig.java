package com.onat.jurist.lawyer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // clients subscribe to /topic/*
        config.setApplicationDestinationPrefixes("/app"); // endpoints from client to server
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // websocket endpoint
                .setAllowedOriginPatterns(
                        "http://localhost:8081",
                        "http://localhost:3000",
                        "https://lawyers-j1tr.onrender.com",
                        "https://onanabeul.netlify.app"
                )                .withSockJS(); // fallback for browsers that don't support WebSocket
    }
}