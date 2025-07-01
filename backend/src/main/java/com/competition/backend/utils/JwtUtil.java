package com.competition.backend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JwtUtil {

    private static final String SECRET_KEY = "your-secret-key";

    public static Integer getTeacherIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
            
            String role = claims.get("role", String.class);
            if (!"teacher".equals(role)) {
                return null;
            }
            
            return claims.get("id", Integer.class);
        } catch (Exception e) {
            return null;
        }
    }
} 