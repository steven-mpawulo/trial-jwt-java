package com.example.trialjwt.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {
    private final String secret = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";
    private Key getsigninkey() {
        byte[] secretBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(secretBytes);

    }
    public String getUserName(String token) {
        return extractClaim(token, (Claims::getSubject));
    }
    public String generateToken(Map<String, Object> claims, String email) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60*48))
                .signWith(getsigninkey(), SignatureAlgorithm.HS256)
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
