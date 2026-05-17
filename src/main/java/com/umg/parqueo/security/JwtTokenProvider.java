package com.umg.parqueo.security;

import com.umg.parqueo.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Proveedor de tokens JWT.
 * Implementa el requerimiento RNF-03 (autenticación con JWT).
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long expirationMs;
    private final String issuer;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs,
            @Value("${app.jwt.issuer}") String issuer) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
        this.issuer = issuer;
    }

    public String generarToken(Usuario usuario) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expirationMs);

        return Jwts.builder()
                .subject(usuario.getCorreo())
                .claim("uid", usuario.getId())
                .claim("rol", usuario.getRol().name())
                .issuer(issuer)
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(key)
                .compact();
    }

    public String obtenerCorreo(String token) {
        return parsearClaims(token).getSubject();
    }

    public Long obtenerUsuarioId(String token) {
        Object uid = parsearClaims(token).get("uid");
        return uid == null ? null : Long.valueOf(uid.toString());
    }

    public boolean validar(String token) {
        try {
            parsearClaims(token);
            return true;
        } catch (Exception ex) {
            log.warn("Token JWT inválido: {}", ex.getMessage());
            return false;
        }
    }

    private Claims parsearClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
