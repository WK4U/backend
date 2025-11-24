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

    public Servico salvarServico( String tipoServico, String cnpj) {
        Optional<Prestador> prestadorOptional = prestadorRepository.findByCnpj(cnpj);
        if (!prestadorOptional.isPresent()) {
            throw new RuntimeException("Prestador não encontrado para o CNPJ: " + cnpj);
        }
        Prestador prestador = prestadorOptional.get();

        // Verifica se já existe serviço igual para esse prestador
        Optional<Servico> existente = servicoRepository.findByTipoServicoAndPrestador(
                 tipoServico, prestador
        );
        if (existente.isPresent()) {
            // Atualiza descrição se mudou
            Servico servico = existente.get();
//            if (!servico.getDescricaoServico().equals(descricaoServico)) {
//                servico.setDescricaoServico(descricaoServico);
//                return servicoRepository.save(servico);
//            }
            return servico;
        }

        // Cria novo se não existir
        Servico servico = new Servico();
//        servico.setNomeServico(nomeServico);
        servico.setTipoServico(tipoServico);
//        servico.setDescricaoServico(descricaoServico);
        servico.setPrestador(prestador);

        return servicoRepository.save(servico);
    }
    public Servico editarServico(Long idServico,String nomeServico, String tipoServico, String descricaoServico){

        if(servicoRepository.findById(idServico).isEmpty()){
            throw new RuntimeException("Serviço não encontrado!");
        }else{

            Servico servico = servicoRepository.findById(idServico).get();

//            if(descricaoServico != null){
//                servico.setDescricaoServico(descricaoServico);
//            }

            if(tipoServico != null){
                servico.setTipoServico(tipoServico);
            }

//            if(nomeServico != null){
//                servico.setNomeServico(nomeServico);
//            }

            return servicoRepository.save(servico);
        }
    }

    public void excluirServ(Long idServico){
        servicoRepository.deleteById(idServico);
    }
}
