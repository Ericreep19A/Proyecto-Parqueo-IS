package com.umg.parqueo.service;

import com.umg.parqueo.dto.response.QrResponse;
import com.umg.parqueo.entity.Estudiante;
import com.umg.parqueo.entity.MarbeteDigital;
import com.umg.parqueo.exception.ReglaNegocioException;
import com.umg.parqueo.util.QRCodeGenerator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Servicio de generación y validación de tokens QR firmados.
 * El QR contiene un JWT corto con: carné, marbete, expiración.
 * Implementa RF06 junto con QRCodeGenerator.
 */
@Service
@RequiredArgsConstructor
public class QrService {

    private final QRCodeGenerator qrGenerator;
    private final EstudianteService estudianteService;
    private final MarbeteService marbeteService;

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.parqueo.qr.expiracion-segundos}")
    private long expiracionSegundos;

    /**
     * Genera el QR del estudiante autenticado.
     * El QR contiene un token firmado con vigencia corta.
     */
     @Transactional(readOnly = true)
public QrResponse generarQrParaEstudiante(String correoEstudiante){
        Estudiante est = estudianteService.obtenerPorCorreo(correoEstudiante);

        // Verifica que tenga marbete vigente (RF05)
        MarbeteDigital marbete;
        try {
            marbete = marbeteService.buscarPorCodigo(
                    marbeteService.obtenerMarbeteVigente(est.getId()).getCodigoUnico());
        } catch (Exception e) {
            throw new ReglaNegocioException("No se puede generar QR: " + e.getMessage());
        }

        if (!marbete.isVigente()) {
            throw new ReglaNegocioException("El marbete del estudiante no está vigente");
        }

        Date ahora = new Date();
        Date expira = new Date(ahora.getTime() + expiracionSegundos * 1000);

        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
                .subject(est.getCarne())
                .claim("estudianteId", est.getId())
                .claim("marbete", marbete.getCodigoUnico())
                .claim("nombre", est.getNombreCompleto())
                .issuer("umg-parqueo-qr")
                .issuedAt(ahora)
                .expiration(expira)
                .signWith(key)
                .compact();

        String imagenBase64 = qrGenerator.generarQrBase64(token);

        return QrResponse.builder()
                .qrToken(token)
                .qrImagenBase64(imagenBase64)
                .emitidoEn(toLocal(ahora))
                .expiraEn(toLocal(expira))
                .build();
    }

    /**
     * Valida el token contenido en un QR y devuelve los claims si es válido.
     * Lanza ReglaNegocioException si está expirado o adulterado.
     */
    public Claims validarToken(String qrToken) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(qrToken)
                    .getPayload();
        } catch (Exception e) {
            throw new ReglaNegocioException("Código QR inválido o expirado");
        }
    }

    private LocalDateTime toLocal(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
