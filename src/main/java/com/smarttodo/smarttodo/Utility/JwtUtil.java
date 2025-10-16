package com.smarttodo.smarttodo.Utility;




import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/*
 *källa https://medium.com/@victoronu/implementing-jwt-authentication-in-a-simple-spring-boot-application-with-java-b3135dbdb17b
 */

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Long expiration;

    /*
     * Detta är en krypteringsnyckel
     * Det används för att signera JWT-tokens när dessa skapas samt när de ska valideras
     * Detta är bara en "platshållare", ingen faktisk nyckel ännu
     * Nyckeln skapas i init() metoden nedan
     */
    private SecretKey key;

    /*
     * Körs automatisk efter att spring har skapat objektet och injicerat alla @Value fält
     * Konverterar strängen secretKey till en byte-array
     * Använder HMAC-SHA aloritm för kryptering
     * Lagrar den krypterade nyckeln i attributet key
     */
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /*
     * Genererar en JWT-token för en användare
     * Tar emot email och userId som parametrar
     * Skapar token med användarnamn som subject och userId som en claim
     * Subjekt är en identifierare för vårt token
     * Claims är ytterligare information som vi vill inkludera i token
     * Vi inkluderar använders Id samt entries
     * Sätter issuedAt till nuvarande tid och expiration till nuvarande tid + expiration (från application.properties)
     * Signerar token med den krypteringsnyckel som skapades i init() metoden
     * Returnerar den genererade token som en sträng
     */
    public String generateToken(String email, Long userId) {
        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime()+ expiration))
                .signWith(key, Jwts.SIG.HS256)
                .compact();

    }

    /*
     * Hämtar email från en JWT-token
     */
    public String getEmailFromToken (String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /*
     * Hämtar userId från en JWT-token
     * Skillnaden mellan ovan och denna är att denna hämtar en claim (userId) medan den ovan hämtar subject (email)
     */
    public Long getUserIdFromToken (String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("userId", Long.class);
    }

    /*
     * Validerar en JWT-token
     * Om token är giltig returneras true
     * Om token är ogiltig fångas olika undantag och false returneras
     */
    public boolean validateToken (String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException e) {
            System.out.println("Invalid JWT signature: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.out.println("JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("JWT claims string is empty: " + e.getMessage());
        }
        return false;
    }
}



