package com.workforyou.backend.service;

import com.workforyou.backend.dto.RegistroRequest;
import com.workforyou.backend.model.PessoaFisica;
import com.workforyou.backend.repository.PessoaFisicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FisicaService {

    @Autowired
    private PessoaFisicaRepository pessoaFisicaRepository;

    public PessoaFisica criarPessoaFisica(RegistroRequest request) {

        PessoaFisica fisica = new PessoaFisica();
        fisica.setCpf(request.getCpf());
        fisica.setNome(request.getNome());
        fisica.setTelefone(request.getTelefone());
        fisica.setDataNascimento(request.getDataNascimento());

        if (fisica.getCpf().isBlank() || fisica.getTelefone().isBlank() || fisica.getDataNascimento() == null) {
            throw new RuntimeException("Informe todos os dados necessários: CPF,NOME,TELEFONE E DATA NASCIMENTO");
        }

        return pessoaFisicaRepository.save(fisica);
    }

    public boolean verificarCpf(String cpf) {
        if (pessoaFisicaRepository.findByCpf(cpf).isPresent()) {
            return false;
        } else {
            return true;
        }
    }

    public PessoaFisica editarPessoaFisica(PessoaFisica pessoaFisica, RegistroRequest request) {
        if (request.getNome() != null && !request.getNome().isBlank()) {
            pessoaFisica.setNome(request.getNome());
        }
        if (request.getTelefone() != null && !request.getTelefone().isBlank()) {
            pessoaFisica.setTelefone(request.getTelefone());
        }
        if (request.getDataNascimento() != null) {
            pessoaFisica.setDataNascimento(request.getDataNascimento());
        }
        return pessoaFisicaRepository.save(pessoaFisica);
    }
}