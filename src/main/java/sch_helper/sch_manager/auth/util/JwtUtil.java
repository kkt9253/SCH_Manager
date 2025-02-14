package sch_helper.sch_manager.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import sch_helper.sch_manager.common.exception.custom.JwtAuthenticationException;
import sch_helper.sch_manager.common.exception.error.ErrorCode;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;

    public JwtUtil(@Value("${spring.jwt.secret}") String secret) {

        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getCategory(String token) {
        return getClaims(token).get("category", String.class);
    }

    public String getUserName(String token) {
        return getClaims(token).getSubject();
    }

    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    public String createJwt(String category, String username, String role, Long expiredS) {
        return Jwts.builder()
                .claim("category", category)
                .subject(username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + (expiredS * 1000)))
                .signWith(secretKey)
                .compact();
    }

    public void validateToken(String token) {
        try {
            getClaims(token);
        } catch (ExpiredJwtException e) {
            System.out.println("Expired JWT token1");
            String category = e.getClaims().get("category", String.class);
            if (category == null || (!category.equals("access") && !category.equals("refresh"))) {
                System.out.println("Expired JWT token2");
                throw new JwtAuthenticationException(ErrorCode.INVALID_TOKEN_CATEGORY);
            }
            System.out.println("Expired JWT token3");
            throw category.equals("access") ? new JwtAuthenticationException(ErrorCode.ACCESS_TOKEN_EXPIRED) : new JwtAuthenticationException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        } catch (MalformedJwtException e) {
            System.out.println("Malformed JWT token");
            throw new JwtAuthenticationException(ErrorCode.JWT_MALFORMED);
        } catch (SignatureException e) {
            System.out.println("Invalid JWT token");
            throw new JwtAuthenticationException(ErrorCode.JWT_SIGNATURE_INVALID);
        } catch (AuthenticationException e) {
            System.out.println("Invalid JWT token AuthenticationException");
            throw new JwtAuthenticationException(ErrorCode.UNAUTHORIZED_ERROR);
        } catch (IllegalArgumentException e) {
            System.out.println("JWT token not provided");
            throw new JwtAuthenticationException(ErrorCode.TOKEN_NOT_PROVIDED);
        } catch (Exception e) {
            System.out.println("JWT token error");
            throw new JwtAuthenticationException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}