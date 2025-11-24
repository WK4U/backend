package com.workforyou.backend.service;


import com.workforyou.backend.model.Postagem;
import com.workforyou.backend.model.Prestador;
import com.workforyou.backend.model.Servico;
import com.workforyou.backend.repository.PostagemRepository;
import com.workforyou.backend.repository.PrestadorRepository;
import com.workforyou.backend.repository.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostagemService {

    @Autowired
    private PostagemRepository postagemRepository;
    @Autowired
    private ServicoRepository servicoRepository;
    @Autowired
    private PrestadorRepository prestadorRepository;

    public Postagem salvarNovaPostagem(String urlFoto,
                                       Long idServico,
                                       String tipoServico,
                                       Long idPrestador,
                                       String descricaoPostagem,
                                       String cnpj
                                       ) {

//        if (postagemJaCriada(nomeServico,tipoServico,cnpj)) {
//            throw new RuntimeException("Postagem já criada com esse serviço!");
//        }

        Postagem postagem = new Postagem();
        postagem.setUrlFoto(urlFoto);
//        postagem.setPrestador(idPrestador.);
        postagem.setTipoServico(tipoServico);
        postagem.setServico(servicoRepository.findById(idServico).get());
        postagem.setDescricaoPostagem(descricaoPostagem);
        postagem.setCnpj(cnpj);

        Prestador prestador = prestadorRepository.findByCnpj(cnpj)
                .orElseThrow(() -> new RuntimeException("Prestador não encontrado para o CNPJ: " + cnpj));
        postagem.setPrestador(prestador);

//        Servico servico = servicoRepository.findById(idServico)
//                .orElseThrow(() -> new RuntimeException("Serviço não encontrado com o ID: " + idServico));
//        postagem.setServico(servico);

        return postagemRepository.save(postagem);
    }


//    public boolean postagemJaCriada( String cnpj) {
//        return postagemRepository.findByNomeServicoAndTipoServicoAndCnpj(cnpj).isPresent();
//    }
    public Postagem editarPostagem(Long idServico,
                                   String urlFoto,
                                   String descricaoPostagem) {

        Postagem postagem = postagemRepository.findByServicoId(idServico)
                .orElseThrow(() -> new RuntimeException("Postagem não encontrada!"));

        if (urlFoto != null) postagem.setUrlFoto(urlFoto);
        if (descricaoPostagem != null) postagem.setDescricaoPostagem(descricaoPostagem);

        return postagemRepository.save(postagem);
    }

    public Postagem atualizarCamposPostagem(Long idPostagem,

                                            String tipoServico,

                                            String descricaoPostagem,
                                            String urlFoto) {
        Postagem p = postagemRepository.findById(idPostagem)
                .orElseThrow(() -> new RuntimeException("Postagem não encontrada!"));

//        if (nomeServico != null) p.setNomeServico(nomeServico);
         if (tipoServico != null) p.setTipoServico(tipoServico);
//        if (descricaoServico != null) p.setDescricaoServico(descricaoServico);
        if (descricaoPostagem != null) p.setDescricaoPostagem(descricaoPostagem);
        if (urlFoto != null) p.setUrlFoto(urlFoto);

        return postagemRepository.save(p);
    }

    public List<Postagem> getPostagemCnpj(String cnpj) {
        return postagemRepository.findByPrestadorPessoaJuridicaCnpj(cnpj)
                .orElseThrow(() -> new RuntimeException("Não há postagens!"));
    }

    public List<Postagem> getPostagem() {
        return postagemRepository.findAll();
    }

    public List<Postagem> getPostagemPorTipo(String tipoServico) {
        return postagemRepository.findByServicoTipoServico(tipoServico)
                .orElseThrow(() -> new RuntimeException("Não há postagens para esse tipo de serviço!"));
    }

    public void excluirPost(Long idPostagem) {
        postagemRepository.deleteById(idPostagem);
    }

    public Postagem getPorId(Long id) {
        return postagemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Postagem não encontrada!"));
    }
}
