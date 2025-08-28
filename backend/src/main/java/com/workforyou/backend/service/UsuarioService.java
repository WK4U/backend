package com.workforyou.backend.service;

import com.workforyou.backend.dto.RegistroRequest;
import com.workforyou.backend.model.*;
import com.workforyou.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service

@RequiredArgsConstructor
public class UsuarioService {


    private final PasswordResetCodeRepository passwordResetCodeRepository;
    private final EmailService emailService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final PessoaFisicaRepository pessoaFisicaRepository;
    private final ClienteRepository clienteRepository;
    private final PrestadorRepository prestadorRepository;

    public void salvarNovoUsuario(RegistroRequest request) {
        // Validação para evitar e-mail duplicado
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Este e-mail já está em uso.");
        }

        // 1. Criar e salvar a PessoaFisica
        PessoaFisica novaPessoa = new PessoaFisica();
        novaPessoa.setNome(request.getNome());

        if (pessoaFisicaRepository.findByCpf(request.getCpf()).isPresent()) {
            throw new RuntimeException("Este CPF já está em uso.");
        }

        novaPessoa.setCpf(request.getCpf());
        novaPessoa.setTelefone(request.getTelefone());
        novaPessoa.setDataNascimento(request.getDataNascimento());
        PessoaFisica pessoaSalva = pessoaFisicaRepository.save(novaPessoa); //  injetar o PessoaFisicaRepository

        // 2. Criar e salvar o Usuario, associando com a PessoaFisica
        Usuario novoUsuario = new Usuario();
        novoUsuario.setEmail(request.getEmail());
        novoUsuario.setSenha(passwordEncoder.encode(request.getSenha())); // Lembre de criptografar a senha
        novoUsuario.setPessoaFisica(pessoaSalva);
        usuarioRepository.save(novoUsuario);

        // 3. Criar o "papel" de Cliente ou Prestador
        if ("CLIENTE".equalsIgnoreCase(request.getTipoUsuario())) {
            Cliente novoCliente = new Cliente();
            novoCliente.setPessoaFisica(pessoaSalva);
            clienteRepository.save(novoCliente); // injetar o ClienteRepository
        } else if ("PRESTADOR".equalsIgnoreCase(request.getTipoUsuario())) {
            Prestador novoPrestador = new Prestador();
            novoPrestador.setPessoaFisica(pessoaSalva);

            novoPrestador.setEspecialidade(request.getEspecialidade());
            novoPrestador.setDescricaoServico(request.getDescricaoServico());

            //  definir outros campos do prestador, se vierem no DTO
            prestadorRepository.save(novoPrestador); // injetar o PrestadorRepository
        } else {
            // Lançar um erro se o tipo de usuário for inválido
            throw new RuntimeException("Tipo de usuário inválido.");
        }
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

        String senhaCriptografada = passwordEncoder.encode(novaSenha);
        usuario.setSenha(senhaCriptografada);
        usuarioRepository.save(usuario);

        resetCode.setUsado(true);
        passwordResetCodeRepository.save(resetCode);

        return true;
    }
}