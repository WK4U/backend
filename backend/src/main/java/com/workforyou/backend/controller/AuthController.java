package com.workforyou.backend.controller;

import com.workforyou.backend.dto.LoginRequest;
import com.workforyou.backend.dto.LoginResponse;
import com.workforyou.backend.dto.RegistroRequest;
import com.workforyou.backend.config.JwtUtil;
import com.workforyou.backend.repository.UsuarioRepository;
import com.workforyou.backend.service.RegistroService;
import com.workforyou.backend.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegistroService registroService;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    @PostMapping(path = "/register" , consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> register(@RequestBody RegistroRequest request) {
        try{
            registroService.salvarNovoUsuario(request);
            return ResponseEntity.ok("Conta registrada com sucesso!");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(409).body(ex.getMessage());
        }
    }

    @PostMapping(path = "/login", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        var usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!usuario.getSenha().equals(request.getSenha())) {
            return ResponseEntity.status(401).body("Senha inválida");
        }

        String token = JwtUtil.gerarToken(usuario.getEmail());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/esqueceu-senha")
    public ResponseEntity<String> esqueceuSenha(@RequestBody Map<String, String> request){
        String email = request.get("email");

        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body("O e-mail é obrigatório");
        }

        usuarioService.solicitarRecuperacaoSenhaPorPin(email);
        return ResponseEntity.ok("Código de redefinição enviado por email");
    }

    @PostMapping("/validar-pin")
    public ResponseEntity<Map<String, String>> validarPin(@RequestBody Map<String, String>request){
        String email = request.get("email");
        String pin = request.get("pin");

        if (email == null || pin == null || email.isBlank() || pin.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Email e PIN são obrigatórios"));
        }

        boolean valido = usuarioService.validarPin(email,pin);
        if (!valido){
            return ResponseEntity.status(400).body(Map.of("erro", "PIN inválido ou expirado"));
        }

        String tokenReset = JwtUtil.gerarToken(email);

        return ResponseEntity.ok((Map.of("tokenReset", tokenReset)));
    }

    @PatchMapping("/redefinir-senha")
    public ResponseEntity<String> redefinirSenha(@RequestBody Map<String, String> request){
        String novaSenha = request.get("novaSenha");
        String code = request.get("code");

        if(novaSenha == null || novaSenha.isBlank()) {
            return ResponseEntity.badRequest().body("Nova senha é obrigatoria");
        }


        boolean atualizado = usuarioService.atualizarSenhaComToken(code, novaSenha);

        if (!atualizado){
            return ResponseEntity.status(400).body("Token inválido ou expirado");
        }

        return ResponseEntity.ok("Senha redefinida com sucesso");
    }

}
