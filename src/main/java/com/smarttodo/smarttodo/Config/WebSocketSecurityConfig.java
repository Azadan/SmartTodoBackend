package com.smarttodo.smarttodo.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

@Configuration
@EnableWebSocketSecurity
public class WebSocketSecurityConfig {

    /*
     * Authorization manager som utvärderar inkommande stompmeddelanden och tillåter eller nekar åtkomst baserat på regler
     */
    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager(
            MessageMatcherDelegatingAuthorizationManager.Builder messages
    ) {
        messages
                // Clienten måste vara autentiserad vid anslutning. Alltså ska en bearer token skickas
                .simpTypeMatchers(SimpMessageType.CONNECT).authenticated()

                .simpTypeMatchers(SimpMessageType.DISCONNECT, SimpMessageType.UNSUBSCRIBE, SimpMessageType.HEARTBEAT).permitAll()

                // Regler för att skicka
                //Vanliga användare och premium kan skicka meddelanden till support-kanalen
                .simpDestMatchers("/app/support/message").hasAnyRole("USER","PREMIUM")
                // Endast admin kan skicka meddelanden till specifika användare
                .simpDestMatchers("/app/support/to/**").hasRole("ADMIN")

                // SUBSCRIBE regler (admin kan prenumerera på /topic/support/admin och users på deras egna köer)
                .simpSubscribeDestMatchers("/topic/support/admin").hasRole("ADMIN")
                .simpSubscribeDestMatchers("/user/queue/support").authenticated()

                // Allt annat kräver autentisering
                .anyMessage().authenticated();

        return messages.build();
    }
}
