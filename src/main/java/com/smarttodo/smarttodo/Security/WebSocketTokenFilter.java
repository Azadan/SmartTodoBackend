package com.smarttodo.smarttodo.Security;

import com.smarttodo.smarttodo.Service.CustomUserDetailService;
import com.smarttodo.smarttodo.Utility.JwtUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketTokenFilter implements ChannelInterceptor {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailService customUserDetailService;

    /*
     * Interceptor som körs innan ett stomp-meddelande hanteras
     * Vid Connect:
     * - Hämtar JWT från STOMP-headern
     * - Validerar token
     * - Laddar användardetaljer och sätter den som user i accessor
     * Detta gör att Spring Messaging känner igen användaren och dess roller
     * under hela websocket-sessionen
     */
    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) return message;

        // Endast vid CONNECT kommando
        if (accessor.getCommand() == StompCommand.CONNECT ||
                accessor.getCommand() == StompCommand.SUBSCRIBE ||
                accessor.getCommand() == StompCommand.SEND) {



            // Kolla ifall en användare redan finns i securityContext
            Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
            if (existingAuth != null && existingAuth.isAuthenticated()) {
                accessor.setUser(existingAuth);
                return message;
            }

            // hämtar token
            String token = parseJwt(accessor);

            if (token != null  && jwtUtil.validateToken(token)) {
                // hämtar email från token
                String email = jwtUtil.getEmailFromToken(token);
                // Laddaren användardetaljer och dess roller baserat på email
                UserDetails userDetails = customUserDetailService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());

                accessor.setUser(auth);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

        }
        return message;

    }

    public String parseJwt(StompHeaderAccessor accessor) {
        String header = accessor.getFirstNativeHeader("Authorization");
        if (header != null && header.trim().startsWith("Bearer ")) {
            return header.trim().substring(7);
        }
        return null;
    }
}
