package com.umg.parqueo.service;

import com.umg.parqueo.dto.request.LoginRequest;
import com.umg.parqueo.dto.response.LoginResponse;
import com.umg.parqueo.entity.Estudiante;
import com.umg.parqueo.entity.Usuario;
import com.umg.parqueo.repository.EstudianteRepository;
import com.umg.parqueo.repository.UsuarioRepository;
import com.umg.parqueo.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UsuarioRepository usuarioRepository;
    private final EstudianteRepository estudianteRepository;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getCorreo(), request.getPassword())
            );
        } catch (Exception ex) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        Usuario usuario = usuarioRepository.findByCorreo(authentication.getName())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        usuario.setUltimoLogin(LocalDateTime.now());
        usuarioRepository.save(usuario);

        String token = tokenProvider.generarToken(usuario);

        String nombreCompleto = estudianteRepository.findByUsuario_Id(usuario.getId())
                .map(Estudiante::getNombreCompleto)
                .orElse(usuario.getCorreo());

        return LoginResponse.builder()
                .token(token)
                .tipo("Bearer")
                .usuarioId(usuario.getId())
                .correo(usuario.getCorreo())
                .rol(usuario.getRol().name())
                .nombreCompleto(nombreCompleto)
                .expiraEnSegundos(expirationMs / 1000)
                .build();
    }
}
