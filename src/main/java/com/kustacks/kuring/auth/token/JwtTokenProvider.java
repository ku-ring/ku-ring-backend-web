package com.kustacks.kuring.auth.token;

import com.kustacks.kuring.auth.exception.UnauthorizedException;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtTokenProperties jwtTokenProperties;

    public String createToken(String principal, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(principal);
        Date now = new Date();
        Date validity = new Date(now.getTime() + jwtTokenProperties.getExpireLength());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .claim("roles", roles)
                .signWith(SignatureAlgorithm.HS256, jwtTokenProperties.getSecretKey())
                .compact();
    }

    public String getPrincipal(String token) {
        try{
            return Jwts.parser()
                    .setSigningKey(jwtTokenProperties.getSecretKey())
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedException();
        }
    }

    public List<String> getRoles(String token) {
        try{
            return Jwts.parser()
                    .setSigningKey(jwtTokenProperties.getSecretKey())
                    .parseClaimsJws(token)
                    .getBody()
                    .get("roles", List.class);
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedException();
        }
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(jwtTokenProperties.getSecretKey()).parseClaimsJws(token);

            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}

