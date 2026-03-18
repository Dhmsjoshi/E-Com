package dev.dharam.authservice.security.util;

import dev.dharam.authservice.models.Role;
import dev.dharam.authservice.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    @Value("${auth.jwt.secret}")
    private String secretKeyStr;

    @Value("${auth.jwt.expiration}")
    private long jwtExpiration;

    public String generateJwtToken(Authentication authentication) {

        var fatchedUser =  (User)authentication.getPrincipal();
        return generateJwtTokenFromUser(fatchedUser);
    }
    public String generateJwtTokenFromUser(User user) {

        SecretKey key = getSigningKey();

        Instant issueInstant = Instant.now();
        Instant expiryInstant = issueInstant.plusMillis(jwtExpiration);

        String roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .issuer("ECOM_APP")
                .subject(user.getId().toString())
                .claim("username", user.getUsername())
                .claim("email", user.getEmail())
                .claim("roles", roles)
                .issuedAt(Date.from(issueInstant))
                .expiration(Date.from(expiryInstant))
                .signWith(key)
                .compact();
    }


    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUserIdFromToken(Claims claims) {
        return claims.getSubject(); // Returns UUID String
    }

    public String getEmailFromToken(Claims claims) {
        return claims.get("email", String.class);
    }

    public String getUsernameFromToken(Claims claims) {
        return claims.get("username", String.class);
    }

    public String getRolesFromToken(Claims claims) {
        Object roles = claims.get("roles");
        return roles != null ? roles.toString() : "";
    }
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKeyStr.getBytes(StandardCharsets.UTF_8));
    }


}
