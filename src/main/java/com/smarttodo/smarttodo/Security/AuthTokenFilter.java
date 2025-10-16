package com.smarttodo.smarttodo.Security;


import com.smarttodo.smarttodo.Exception.UserAuthenticationException;
import com.smarttodo.smarttodo.Service.CustomUserDetailService;
import com.smarttodo.smarttodo.Utility.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final CustomUserDetailService customUserDetailService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // Hämtar JWT från förfrågan
            String jwt = parseJwt(request);
            // Om token inte är null och är giltig
            if (jwt != null && jwtUtil.validateToken(jwt)) {
                // Hämtar användarnamn från token
                String email = jwtUtil.getEmailFromToken(jwt);
                // Laddar användardetaljer baserat på användarnamnet
                UserDetails userDetails = customUserDetailService.loadUserByUsername(email);
                // Skapar en autentiseringstoken
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                // Sätter autentiseringen i säkerhetskontexten
                // Detta är spring securitys sätt att hålla reda på vem som är inloggad
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (UserAuthenticationException e) {
            System.out.println("Cannot set user authentication: " + e);
        }
        // Denna ser till att förfrågan fortsätter genom filterkedjan
        filterChain.doFilter(request, response);
    }

    /*
     * Denna methoden hämtar JWT från HTTP-förfrågans Authorization-header
     */
    private String parseJwt(HttpServletRequest request) {
        // Först hämtar vi Authorization-headern från förfrågan
        String headerAuth = request.getHeader("Authorization");
        // Kontrollerar om headern inte är null och börjar med "Bearer "
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            // Returnerar själva token genom att ta bort "Bearer " prefixet
            return headerAuth.substring(7);
        } else {
            return null;
        }
    }
}
