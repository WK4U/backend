package com.workforyou.backend.service;
import com.workforyou.backend.model.*;
import com.workforyou.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService; // NOVA IMPORTAÇÃO
import org.springframework.security.core.userdetails.UsernameNotFoundException; // NOVA IMPORTAÇÃO
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User; // NOVA IMPORTAÇÃO - Classe User do Spring

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Collections; // NOVA IMPORTAÇÃO para listar de permissões vazia
import java.util.Optional;

@Service
// Implementar a interface UserDetailsService é CRÍTICO para o Spring Security
public class UsuarioService implements UserDetailsService {

    @Autowired
    private PasswordResetCodeRepository passwordResetCodeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    // MÉTODO OBRIGATÓRIO DA INTERFACE UserDetailsService
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Usa o repositório para buscar o usuário pelo e-mail (que é o subject do seu token)
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        // Retorna um objeto UserDetails do Spring Security
        // Passamos o email, a senha (armazenada), e uma lista vazia para 'authorities'
        // Se você tiver roles (ex: "ADMIN", "USER"), coloque-as aqui.
        return new User(
                usuario.getEmail(),
                usuario.getSenha(),
                Collections.emptyList() // Nenhuma permissão/role específica no momento
        );
    }

    // Seu código original a partir daqui...

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
