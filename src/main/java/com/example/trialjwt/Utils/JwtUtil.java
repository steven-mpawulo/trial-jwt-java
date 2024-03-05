package com.example.trialjwt.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

@Service
public class JwtUtil {
    private String secret = "secret";
    String getUserName(String token) {
        return Jwts.parser().setSigningKey(secret).build().parseSignedClaims(token).getBody().getSubject();
    }
}
