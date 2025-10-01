package com.workforyou.backend.service;

import com.workforyou.backend.dto.RegistroRequest;
import com.workforyou.backend.model.*;
import com.workforyou.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service

@RequiredArgsConstructor
public class UsuarioService {


    @Autowired
    private final PasswordResetCodeRepository passwordResetCodeRepository;

    @Autowired
    private final EmailService emailService;

    @Autowired
    private final UsuarioRepository usuarioRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final PessoaFisicaRepository pessoaFisicaRepository;

    @Autowired
    private final ClienteRepository clienteRepository;

    @Autowired
    private final PrestadorRepository prestadorRepository;

    @Autowired
    private final PessoaJuridicaRepository pessoaJuridicaRepository;

    public void salvarNovoUsuario(RegistroRequest request) {
        // Validação para evitar e-mail duplicado
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Este e-mail já está em uso.");
        }

        if (request.getTipoUsuario().equalsIgnoreCase("FISICO")) {
            if (pessoaFisicaRepository.findByCpf(request.getCpf()).isPresent()) {
                throw new RuntimeException("Este CPF já está em uso.");
            } else {
                PessoaFisica fisica = new PessoaFisica();

                fisica.setCpf(request.getCpf());
                fisica.setNome(request.getNome());
                fisica.setTelefone(request.getTelefone());
                fisica.setDataNascimento(request.getDataNascimento());

                pessoaFisicaRepository.save(fisica);

                Usuario userFisico = new Usuario();
                userFisico.setSenha(request.getSenha()); // A senha já vem criptografada pelo front-end
                userFisico.setTipoUsuario('f');
                userFisico.setDocumento(fisica.getCpf());

                usuarioRepository.save(userFisico);

                Cliente cliente = new Cliente();
                cliente.setUrlFoto(request.getUriFoto());
                cliente.setEmail(request.getEmail());
                cliente.setPessoaFisica(fisica);

                clienteRepository.save(cliente);
            }
        }
        if (request.getTipoUsuario().equalsIgnoreCase("JURIDICO")) {
            if (pessoaJuridicaRepository.findByCnpj(request.getCnpj()).isPresent()) {
                throw new RuntimeException("Este CNPJ já está em uso.");
            } else {
                PessoaJuridica juridica = new PessoaJuridica();

                juridica.setNome(request.getNome());
                juridica.setCnpj(request.getCnpj());
                juridica.setTelefone(request.getTelefone());

                pessoaJuridicaRepository.save(juridica);

                Usuario userJuridico = new Usuario();
                userJuridico.setDocumento(juridica.getCnpj());
                userJuridico.setTipoUsuario('j');
                userJuridico.setSenha(request.getSenha());

                usuarioRepository.save(userJuridico);

                Prestador prestador = new Prestador();
                prestador.setEmail(request.getEmail());
                prestador.setEspecialidade(request.getEspecialidade());
                prestador.setUrlFoto(request.getUriFoto());
                prestador.setPessoaJuridica(juridica);

                prestadorRepository.save(prestador);
            }
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