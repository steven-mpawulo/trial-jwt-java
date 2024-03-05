package com.example.trialjwt.Utils;

import com.example.trialjwt.models.MyUserDetails;
import com.example.trialjwt.services.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Service
public class JwtUtil {
    private final String secret = "secret";
    private Key getSigningKey() {
        byte[] secretBytes = Decoders.BASE64.decode(this.secret);
        return Keys.hmacShaKeyFor(secretBytes);

    }
    String getUserName(String token) {
        return Jwts.parser().setSigningKey(getSigningKey()).build().parseSignedClaims(token).getBody().getSubject();
    }
    String generateToken(Map<String, Object> claims, String email) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60))
                .signWith(getSigningKey())
                .compact();
    }
}
