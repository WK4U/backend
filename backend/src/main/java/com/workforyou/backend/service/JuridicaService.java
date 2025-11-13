package com.workforyou.backend.service;

import com.workforyou.backend.dto.RegistroRequest;
import com.workforyou.backend.model.PessoaJuridica;
import com.workforyou.backend.repository.PessoaJuridicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JuridicaService {

    @Autowired
    private PessoaJuridicaRepository pessoaJuridicaRepository; // Você precisará deste repositório

    public PessoaJuridica criarPessoaJuridica(RegistroRequest request) {

        PessoaJuridica juridica = new PessoaJuridica();
        juridica.setNome(request.getNome());
        juridica.setCnpj(request.getCnpj());
        juridica.setTelefone(request.getTelefone());

        return pessoaJuridicaRepository.save(juridica);
    }

    public boolean verificarCnpj(String cnpj){
        if(pessoaJuridicaRepository.findByCnpj(cnpj).isPresent()){
            return false;
        }else{
            return true;
        }
    }

    public PessoaJuridica editarPessoaJuridica(PessoaJuridica pessoaJuridica, RegistroRequest request) {
        if (request.getNome() != null && !request.getNome().isBlank()) {
            pessoaJuridica.setNome(request.getNome());
        }
        if (request.getTelefone() != null && !request.getTelefone().isBlank()) {
            pessoaJuridica.setTelefone(request.getTelefone());
        }
        return pessoaJuridicaRepository.save(pessoaJuridica);
    }
}

