package com.workforyou.backend.service;

import com.workforyou.backend.model.Servico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostagemServicoService {

    @Autowired
    private ServicoService servicoService;

    @Autowired
    private PostagemService postagemService;

    public void criarPostagemServico(String nomeServico, String tipoServico, String descricaoS,String descricaoP, String cnpj, String foto){

        // 1. Salva o serviço
        Servico servico = servicoService.salvarServico(nomeServico,tipoServico,descricaoS,cnpj);

        // 2. Salva a postagem com a ordem CORRETA de argumentos:
        // Ordem esperada: (foto, descricaoPostagem, cnpj, idServico)
        postagemService.salvarNovaPostagem(
                foto,
                descricaoP, // Argumento 2: Descrição da Postagem (descricaoPostagem)
                cnpj,       // Argumento 3: CNPJ
                servico.getId()
        );
    }
}
