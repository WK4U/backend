package com.workforyou.backend.controller;

import com.workforyou.backend.dto.LoginRequest;
import com.workforyou.backend.dto.LoginResponse;
import com.workforyou.backend.model.Usuario;
import com.workforyou.backend.repository.UsuarioRepository;
import com.workforyou.backend.config.JwtUtil;
import com.workforyou.backend.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping(path = "/register" , consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> register(@RequestBody Usuario usuario) {
        usuarioService.salvarUsuario(usuario);
        return ResponseEntity.ok("Usuário registrado com sucesso!");
    }

    @PostMapping(path = "/login", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        var usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenha())) {
            return ResponseEntity.status(401).body("Senha inválida");
        }

        String token = JwtUtil.gerarToken(usuario.getEmail());
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
