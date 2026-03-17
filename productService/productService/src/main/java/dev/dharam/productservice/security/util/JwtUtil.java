package dev.dharam.productservice.security.util;


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
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    @Value("${auth.jwt.secret}")
    private String secretKeyStr;

    @Value("${auth.jwt.expiration}")
    private long jwtExpiration;

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUsernameFromToken(Claims claims) {
        return String.valueOf(claims.get("username"));
    }

    public UUID getUserIdFromToken(Claims claims) {
        return UUID.fromString(claims.getSubject());
    }

    public String getEmailFromToken(Claims claims) {
        return String.valueOf(claims.get("email"));
    }

    public String getRolesFromToken(Claims claims) {
        return claims.get("roles", String.class);

    }
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKeyStr.getBytes(StandardCharsets.UTF_8));
    }


}
