package com.workforyou.backend.service;

import com.workforyou.backend.dto.RegistroRequest;
import com.workforyou.backend.model.PessoaJuridica;
import com.workforyou.backend.repository.PessoaJuridicaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JuridicaService {

    private final PessoaJuridicaRepository pessoaJuridicaRepository; // Você precisará deste repositório

    public PessoaJuridica criarPessoaJuridica(RegistroRequest request) {

        PessoaJuridica juridica = new PessoaJuridica();
        juridica.setNome(request.getNome());
        juridica.setCnpj(request.getCnpj());
        juridica.setTelefone(request.getTelefone());

        return pessoaJuridicaRepository.save(juridica);
    }
}
