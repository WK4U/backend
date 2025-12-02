package com.workforyou.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workforyou.backend.config.JwtUtil;
import com.workforyou.backend.dto.LoginRequest;
import com.workforyou.backend.dto.LoginResponse;
import com.workforyou.backend.dto.RegistroRequest;
import com.workforyou.backend.dto.UsuarioPerfilResponse;
import com.workforyou.backend.model.Cliente;
import com.workforyou.backend.model.Prestador;
import com.workforyou.backend.model.Usuario;
import com.workforyou.backend.repository.ClienteRepository;
import com.workforyou.backend.repository.PrestadorRepository;
import com.workforyou.backend.repository.UsuarioRepository;
import com.workforyou.backend.service.RegistroService;
import com.workforyou.backend.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegistroService registroService;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final ObjectMapper objectMapper;
    private final ClienteRepository clienteRepository;
    private final PrestadorRepository  prestadorRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping(path = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> register(
            @RequestPart("dados") String dadosJson,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        try {
            RegistroRequest request = objectMapper.readValue(dadosJson, RegistroRequest.class);
            registroService.salvarNovoUsuario(request, file);
            return ResponseEntity.ok("Conta registrada com sucesso!");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(409).body(ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("JSON inválido em 'dados'");
        }
    }

    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return usuarioRepository.findByEmail(request.getEmail())
                .map(usuario -> {
                    if (!passwordEncoder.matches(request.getSenha(), usuario.getSenha())) {
                        return ResponseEntity.status(401).body("Senha inválida");
                    }
                    String token = JwtUtil.gerarToken(usuario.getEmail());
                    return ResponseEntity.ok(new LoginResponse(token));
                })
                .orElseGet(() -> ResponseEntity.status(404).body("Não encontrado!"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Usuário não autenticado");
        }
        UsuarioPerfilResponse perfil = usuarioService.obterPerfil(principal.getName());
        return ResponseEntity.ok(perfil);
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

    @PatchMapping(path = "/redefinir-senha", consumes = MediaType.APPLICATION_JSON_VALUE)
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

    @PatchMapping(path = "/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> editarConta(
            @RequestPart("dados") String dadosJson,
            @RequestPart(value = "file", required = false) MultipartFile file,
            Principal principal
    ) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Usuário não autenticado.");
        }
        try {
            RegistroRequest request = objectMapper.readValue(dadosJson, RegistroRequest.class);
            String emailLogado = principal.getName();
            registroService.editarUsuario(emailLogado, request, file);
            return ResponseEntity.ok("Perfil atualizado com sucesso!");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("JSON inválido em 'dados'");
        }
    }
}