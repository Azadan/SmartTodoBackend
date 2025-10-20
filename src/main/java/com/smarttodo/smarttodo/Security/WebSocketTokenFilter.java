package com.smarttodo.smarttodo.Security;

import com.smarttodo.smarttodo.Exception.UserAuthenticationException;
import com.smarttodo.smarttodo.Service.CustomUserDetailService;
import com.smarttodo.smarttodo.Utility.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) return message;
        // Endast vid CONNECT kommando
        if (accessor.getCommand() == StompCommand.CONNECT) {
            // hämtar token
            String token = parseJwt(accessor);
            //error hantering
            if (token == null || !jwtUtil.validateToken(token)) throw new UserAuthenticationException("JWT token is null");

            // hämtar email från token
            String email = jwtUtil.getEmailFromToken(token);
            // Laddaren användardetaljer och dess roller baserat på email
            UserDetails userDetails = customUserDetailService.loadUserByUsername(email);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities());

            accessor.setUser(auth);
        }
        return message;

    }

    public String parseJwt(StompHeaderAccessor accessor) {
        String token = accessor.getFirstNativeHeader("Authorization");

        String jwt = null;
        if (token != null) {
            jwt = token.substring(7);
        }
        return jwt;
    }

}
