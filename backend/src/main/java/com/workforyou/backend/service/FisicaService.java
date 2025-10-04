package com.workforyou.backend.service;

import com.workforyou.backend.dto.RegistroRequest;
import com.workforyou.backend.model.PessoaFisica;
import com.workforyou.backend.repository.PessoaFisicaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FisicaService {

    private final PessoaFisicaRepository pessoaFisicaRepository;

    public PessoaFisica criarPessoaFisica(RegistroRequest request) {

        PessoaFisica fisica = new PessoaFisica();
        fisica.setCpf(request.getCpf());
        fisica.setNome(request.getNome());
        fisica.setTelefone(request.getTelefone());
        // Lembre-se que você pode precisar de um conversor de String para Calendar
        // fisica.setDataNascimento(request.getDataNascimento()); // Assumindo Calendar

        return pessoaFisicaRepository.save(fisica);
    }
}