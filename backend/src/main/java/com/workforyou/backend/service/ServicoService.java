package com.workforyou.backend.service;


import com.workforyou.backend.model.Prestador;
import com.workforyou.backend.model.Servico;
import com.workforyou.backend.repository.PrestadorRepository;
import com.workforyou.backend.repository.ServicoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ServicoService {

    @Autowired
    private ServicoRepository servicoRepository;
    @Autowired
    private PrestadorRepository prestadorRepository;

    public Servico salvarServico(String nomeServico, String tipoServico, String descricaoServico, String cnpj){
        Servico servico = new Servico();
        servico.setNomeServico(nomeServico);
        servico.setTipoServico(tipoServico);
        servico.setDescricaoServico(descricaoServico);

        Optional<Prestador> prestadorOptional = prestadorRepository.findByCnpj(cnpj);

        if(prestadorOptional.isPresent()){
            servico.setPrestador(prestadorOptional.get());
        }else{
            throw new RuntimeException("Prestador não encontrado para o CNPJ: " + cnpj);
        }

        return servicoRepository.save(servico);
    }

    public Servico editarServico(Long idServico,String nomeServico, String tipoServico, String descricaoServico){

        if(servicoRepository.findById(idServico).isEmpty()){
            throw new RuntimeException("Serviço não encontrado!");
        }else{

            Servico servico = servicoRepository.findById(idServico).get();

            servico.setDescricaoServico(descricaoServico);
            servico.setTipoServico(tipoServico);
            servico.setNomeServico(nomeServico);

            return servicoRepository.save(servico);
        }
    }

    public void excluirServ(Long idServico){
        servicoRepository.deleteById(idServico);
    }
}
