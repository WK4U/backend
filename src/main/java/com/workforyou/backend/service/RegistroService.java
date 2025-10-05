package com.workforyou.backend.service;

import com.workforyou.backend.dto.RegistroRequest;
import com.workforyou.backend.model.PessoaFisica;
import com.workforyou.backend.model.PessoaJuridica;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistroService {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private FisicaService fisicaService;

    @Autowired
    private JuridicaService juridicaService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private PrestadorService prestadorService;

    public void salvarNovoUsuario(RegistroRequest request) {
        if(usuarioService.verificarEmailExistente(request.getEmail())){
            throw new RuntimeException("Este e-mail já está em uso.");
        }

        if (request.getTipoUsuario().equalsIgnoreCase("FISICO")) {
            salvarUsuarioFisico(request);
        } else if (request.getTipoUsuario().equalsIgnoreCase("JURIDICO")) {
            salvarUsuarioJuridico(request);
        } else {
            throw new IllegalArgumentException("Tipo de usuário inválido.");
        }
    }

    private void salvarUsuarioFisico(RegistroRequest request) {

        if(fisicaService.verificarCpf(request.getCpf())){
            // 1. Cria PessoaFisica
            PessoaFisica fisica = fisicaService.criarPessoaFisica(request);

            // 2. Cria Cliente
            clienteService.criarCliente(request, fisica);

            // 3. Cria Usuário de Login
            usuarioService.criarUsuario(request.getEmail(), request.getSenha(), 'f', fisica.getCpf());
        }else{
            throw new RuntimeException("CPF JÁ CADASTRADO!");
        }

    }

    private void salvarUsuarioJuridico(RegistroRequest request) {

        if(juridicaService.verificarCnpj(request.getCnpj())){
            // 1. Cria PessoaJuridica
            PessoaJuridica juridica = juridicaService.criarPessoaJuridica(request);

            // 2. Cria Prestador
            prestadorService.criarPrestador(request, juridica);

            // 3. Cria Usuário de Login
            usuarioService.criarUsuario(request.getEmail(), request.getSenha(), 'j', juridica.getCnpj());
        }else{
            throw new RuntimeException("CNPJ JÁ CADASTRADO!");
        }


    }
}