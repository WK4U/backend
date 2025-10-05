package com.workforyou.backend.service;


import com.workforyou.backend.model.Postagem;
import com.workforyou.backend.model.Prestador;
import com.workforyou.backend.model.Servico;
import com.workforyou.backend.repository.PostagemRepository;
import com.workforyou.backend.repository.PrestadorRepository;
import com.workforyou.backend.repository.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostagemService {

    @Autowired
    private PostagemRepository postagemRepository;
    @Autowired
    private ServicoRepository servicoRepository;
    @Autowired
    private PrestadorRepository prestadorRepository;

    public Postagem salvarNovaPostagem(String foto,String descricaoPostagem,String cnpj,Long idServico){
        if(postagemJaCriada(idServico)){

            throw new RuntimeException("Postagem já criada com esse serviço!");

        }else {

            Postagem postagem = new Postagem();
            postagem.setUrlFoto(foto);
            postagem.setDescricaoPostagem(descricaoPostagem);

            Optional<Prestador> prestadorOptional = prestadorRepository.findByCnpj(cnpj);

            if (prestadorOptional.isPresent()) {
                postagem.setPrestador(prestadorOptional.get());
            } else {
                throw new RuntimeException("Prestador não encontrado para o CNPJ: " + cnpj);
            }

            Optional<Servico> servicoOptional = servicoRepository.findById(idServico);

            if (servicoOptional.isPresent()) {
                postagem.setServico(servicoOptional.get());
            } else {
                throw new RuntimeException("Serviço não encontrado com o ID: " + idServico);
            }

            return postagemRepository.save(postagem);
        }
    }

    public boolean postagemJaCriada(Long idServico){
        if(postagemRepository.findByServicoId(idServico).isPresent()){
            return true;
        }else{
            return false;
        }
    }
}

