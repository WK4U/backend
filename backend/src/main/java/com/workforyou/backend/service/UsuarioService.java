package com.workforyou.backend.service;
import com.workforyou.backend.model.*;
import com.workforyou.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final PasswordResetCodeRepository passwordResetCodeRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;

    public Usuario criarUsuario(String email, String senha, char tipoUsuario, String documento) {

        Usuario user = new Usuario();
        user.setEmail(email);
        user.setSenha(senha); // A senha já vem criptografada
        user.setTipoUsuario(tipoUsuario);
        user.setDocumento(documento); // CPF ou CNPJ

        return usuarioRepository.save(user);
    }


   public void solicitarRecuperacaoSenhaPorPin(String email){
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontardo"));

        String pin = String.format("%05d", new SecureRandom().nextInt(100000));

       PasswordResetCode resetCode = new PasswordResetCode();
       resetCode.setCode(pin);
       resetCode.setExpiracao(LocalDateTime.now().plusMinutes(5));
       resetCode.setUsado(false);
       resetCode.setUsuario(usuario);

       passwordResetCodeRepository.save(resetCode);

        String mensagem = "Seu código de redefinição de senha é: " + pin
                + "\nEle expira em 5 minutos.";
        emailService.send(usuario.getEmail(), "Código de redefinição de senha", mensagem);
   }

    public boolean validarPin(String email, String code) {
        Optional<PasswordResetCode> opt = passwordResetCodeRepository.findByUsuarioEmailAndCode(email, code);

        if (opt.isEmpty()) {
            return false;
        }

        PasswordResetCode resetCode = opt.get();

        if (resetCode.getExpiracao().isBefore(LocalDateTime.now())) {
            return false;
        }

        if (resetCode.isUsado()) {
            return false;
        }

        return true;
    }

    public boolean atualizarSenhaComToken(String code, String novaSenha) {

        Optional<PasswordResetCode> opt = passwordResetCodeRepository.findByCode(code);

        if (opt.isEmpty()) {
            return false;
        }

        PasswordResetCode resetCode = opt.get();


        if (resetCode.isUsado() || resetCode.getExpiracao().isBefore(LocalDateTime.now())) {
            return false;
        }

        Usuario usuario = resetCode.getUsuario();

        usuario.setSenha(novaSenha);
        usuarioRepository.save(usuario);

        resetCode.setUsado(true);
        passwordResetCodeRepository.save(resetCode);

        return true;
    }

    public boolean verificarEmailExistente(String email){
        if(usuarioRepository.findByEmail(email).isPresent()){
            return true;
        }else{
            return false;
        }
    }
}