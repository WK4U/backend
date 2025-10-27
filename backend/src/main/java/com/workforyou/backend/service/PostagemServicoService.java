package com.workforyou.backend.service;

import com.workforyou.backend.model.Postagem;
import com.workforyou.backend.model.Servico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostagemServicoService {

    @Autowired
    private ServicoService servicoService;

    @Autowired
    private PostagemService postagemService;

    public void salvarPostagemServico(String nomeServico, String tipoServico, String descricaoS, String descricaoP, String cnpj, String foto){

        Servico servico = servicoService.salvarServico(nomeServico,tipoServico,descricaoS,cnpj);

        postagemService.salvarNovaPostagem(
                foto,
                descricaoP,
                cnpj,
                servico.getId()
        );
    }

    public void editarPostagemServico(Long idServico,String nomeServico,String tipoServico,String descricaoServico,String descricaoPostagem,String foto){

        Servico servico = servicoService.editarServico(idServico,nomeServico,tipoServico,descricaoServico);

        postagemService.editarPostagem(servico.getId(),foto,descricaoPostagem);
    }

    public List<Postagem> getPostagensPorCnpj(String cnpj){
        return postagemService.getPostagemCnpj(cnpj);
    }

    public List<Postagem> getPostagens(){
        return postagemService.getPostagem();
    }

    public List<Postagem> getPostagensTipoServico(String tipoServico){
        return postagemService.getPostagemPorTipo(tipoServico);
    }
    public void excluirPostagemServico(Long idServico, Long idPostagem){
        postagemService.excluirPost(idPostagem);

        servicoService.excluirServ(idServico);
    }
}
