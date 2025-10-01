package com.workforyou.backend.service;


import com.workforyou.backend.model.Prestador;
import com.workforyou.backend.model.Servico;
import com.workforyou.backend.repository.PrestadorRepository;
import com.workforyou.backend.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServicoService {

    @Autowired
    private ServicoRepository servicoRepository;
    @Autowired
    private PrestadorRepository prestadorRepository;

    public void salvarServico(String nomeServico, String tipoServico, String descricao, String cnpj){
        Servico servico = new Servico();
        servico.setNomeServico(nomeServico);
        servico.setTipoServico(tipoServico);
        servico.setDescricao(descricao);

        Optional<Prestador> prestadorOptional = prestadorRepository.findByPessoaJuridicaCnpj(cnpj);

        if(prestadorOptional.isPresent()){
            servico.setPrestador(prestadorOptional.get());
        }else{
            throw new RuntimeException("Prestador não encontrado para o CNPJ: " + cnpj);
        }

        servicoRepository.save(servico);
    }
}
