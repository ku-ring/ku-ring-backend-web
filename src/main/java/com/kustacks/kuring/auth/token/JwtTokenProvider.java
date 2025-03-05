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

    public String createAdminToken(String principal, List<String> roles) {
        Date validity = new Date(new Date().getTime() + jwtTokenProperties.adminExpireLength());

        return getJwtBuilderWithPrincipal(principal)
                .claim("roles", roles)
                .setExpiration(validity)
                .compact();
    }

    public String createUserToken(String principal) {
        Date validity = new Date(new Date().getTime() + jwtTokenProperties.userExpireLength());

        return getJwtBuilderWithPrincipal(principal)
                .setExpiration(validity)
                .compact();
    }


    public String getPrincipal(String token) {
        try{
            return Jwts.parser()
                    .setSigningKey(jwtTokenProperties.secretKey())
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
                    .setSigningKey(jwtTokenProperties.secretKey())
                    .parseClaimsJws(token)
                    .getBody()
                    .get("roles", List.class);
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedException();
        }
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(jwtTokenProperties.secretKey()).parseClaimsJws(token);

            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private JwtBuilder getJwtBuilderWithPrincipal(String principal) {
        Claims claims = Jwts.claims().setSubject(principal);
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.HS256, jwtTokenProperties.secretKey());
    }
}

