package com.workforyou.backend.service;

import com.workforyou.backend.model.PessoaFisica;
import com.workforyou.backend.repository.PessoaFisicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class FisicaService {

    @Autowired
    PessoaFisicaRepository pessoaFisicaRepository;

    public void salvarNovaPessoaFisica(String nome, String cpf, String telefone, Calendar dataNascimento){
        PessoaFisica pessoaFisica = new PessoaFisica();
        pessoaFisica.setNome(nome);
        pessoaFisica.setCpf(cpf);
        pessoaFisica.setTelefone(telefone);
        pessoaFisica.setDataNascimento(dataNascimento);

        pessoaFisicaRepository.save(pessoaFisica);
    }
}
