package com.smarttodo.smarttodo.Config;

import com.smarttodo.smarttodo.Security.AuthEntryPointJwt;
import com.smarttodo.smarttodo.Security.AuthTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final AuthEntryPointJwt unAuthorizedHandler;

    private final AuthTokenFilter authTokenFilter;

    /*
     * En bean för att hantera autentisering
     * @Param authenticationConfiguration är konfigurationen för autentisering
     * @Return AuthenticationManager som hanterar autentisering
     */

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Pga vi inte har en session så har vi ingen CSRF-risk. Därav disablar vi det.
                .csrf(csrf -> csrf.disable())
                //Letar efter CORS-konfigurationen i corsConfigurationSource() metoden nedan
                .cors(Customizer.withDefaults())
                // Hantera obehöriga försök att få åtkomst till skyddade resurser. Åkallar vår AuthEntryPointJwt klass
                .exceptionHandling(exceptionHandler ->
                        exceptionHandler.authenticationEntryPoint(unAuthorizedHandler))
                // Gör så att vi inte skapar en session. Varje request måste vara autentiserad
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Definierar vilka endpoints som är publika och vilka som kräver autentisering
                // Endast endpoints för registrering och autentisering är publika
                .authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests
                                .requestMatchers("/api/v1/auth/**").permitAll()
                                .requestMatchers("/api/v1/users/**").permitAll()
                                .anyRequest().authenticated()
                );
        // Lägger till vår AuthTokenFilter innan UsernamePasswordAuthenticationFilter i filterkedjan
        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /*
     * Konfigurerar CORS för att tillåta förfrågningar från vår frontend applikation
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Definierar vilka origins som är tillåtna att göra förfrågningar från
        config.setAllowedOrigins(List.of("*"));
        // Definerar vilka HTTP metoder som är tillåtna.
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Definierar vilka headers som är tillåtna
        config.setAllowedHeaders(List.of("*"));
        // Tillåter cookies och andra credentials att skickas med förfrågningar
        config.setAllowCredentials(true);
        // Applicerar denna CORS konfiguration på alla endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
