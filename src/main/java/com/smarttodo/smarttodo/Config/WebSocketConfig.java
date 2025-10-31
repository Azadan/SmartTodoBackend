package com.smarttodo.smarttodo.Config;

import com.smarttodo.smarttodo.Security.WebSocketTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // Filter som validerar JWT token vid STOMP anslutning
    private final WebSocketTokenFilter webSocketTokenFilter;


    /*
     * Konfiguerar  meddelandebroker för WebSocket
     * enableSimpleBroker: inbyggd in-memory broker för att hantera meddelanden till klienter
     * setUserDestinationPrefix: prefix för användarspecifika destinationer
     * setApplicationDestinationPrefixes: prefix för meddelanden som skickas från klienter till servern
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setUserDestinationPrefix("/user");
        config.setApplicationDestinationPrefixes("/app");
    }

    /*
     * Registrerar STOMP endpoints som klienten ansluter till
     * addEndpoint("/ws"): definierar endpoint för WebSocket anslutningar
     * setAllowedOriginPatterns: tillåter CORS för angivna ursprung
     * withSockJS(): aktiverar SockJS fallback-alternativ för äldre webbläsare
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost:4200", "http://localhost:63343","https://sea-lion-app-9rbwy.ondigitalocean.app/")
                .withSockJS();
    }

    /*
     * Lägger till interceptor för inkommande kanal så att JWT extraher och valideras
     * Detta sker vid connect
     * Efter autentisering har skett sätts användare så att roller och destinationer fungerar
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketTokenFilter);
    }
}
