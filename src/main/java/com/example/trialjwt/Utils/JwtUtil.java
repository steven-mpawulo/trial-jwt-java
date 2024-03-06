package com.example.trialjwt.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {
    private final String secret = "secret";
    private Key getsigninkey() {
        return Jwts.SIG.HS256.key().build();

    }
    public String getUserName(String token) {
        return extractClaim(token, (Claims::getSubject));
    }
    public String generateToken(Map<String, Object> claims, String email) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60))
                .signWith(getsigninkey())
                .compact();
    }
    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(getsigninkey()).build().parseSignedClaims(token).getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    public boolean hasTokenExpired(String token) {
        return extractClaim(token, (Claims::getExpiration)).before(new Date());
    }
    public boolean isTokenValid(String token, UserDetails userDetails) {
        return getUserName(token).equals(userDetails.getUsername()) && !hasTokenExpired(token);
    }
}
