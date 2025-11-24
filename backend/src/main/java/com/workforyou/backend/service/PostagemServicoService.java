package com.workforyou.backend.service;

import com.workforyou.backend.dto.PostagemRequest;
import com.workforyou.backend.model.Postagem;
import com.workforyou.backend.model.Prestador;
import com.workforyou.backend.model.Servico;
import com.workforyou.backend.model.Usuario;
import com.workforyou.backend.repository.PostagemRepository;
import com.workforyou.backend.repository.PrestadorRepository;
import com.workforyou.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class PostagemServicoService {

    @Autowired
    private ServicoService servicoService;
    @Autowired
    private PostagemService postagemService;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private PostagemRepository postagemRepository;
    @Autowired
    private PrestadorRepository prestadorRepository;

    public void salvarPostagemServico(
            String tipoServico,
            String descricaoPostagem,
            String cnpj,
            MultipartFile foto
    ) {

        String urlFoto = null;
        if (foto != null && !foto.isEmpty()) {
            urlFoto = cloudinaryService.uploadImagem(foto);
        }

        Servico servico = servicoService.salvarServico(tipoServico, cnpj);

        Prestador prestador = prestadorRepository.findByCnpj(cnpj)
                .orElseThrow(() -> new RuntimeException("Prestador não encontrado para o CNPJ: " + cnpj));

        Postagem postagem = new Postagem();
        postagem.setUrlFoto(urlFoto);
        postagem.setTipoServico(tipoServico);
        postagem.setDescricaoPostagem(descricaoPostagem);
        postagem.setCnpj(cnpj);

        postagem.setPrestador(prestador);
        postagem.setServico(servico);  // <--- ESSA LINHA RESOLVE A DUPLICAÇÃO

        postagemRepository.save(postagem);
    }


    public void editarPostagemServico(String emailLogado,
                                      Long idPostagem,
                                      PostagemRequest request,
                                      MultipartFile novaFoto) {

        Usuario usuarioLogado = usuarioRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com esse email!"));

        Postagem postagem = postagemService.getPorId(idPostagem);

        if (!postagem.getPrestador().getPessoaJuridica().getCnpj()
                .equals(usuarioLogado.getDocumento())) {
            throw new RuntimeException("Acesso negado. Você não é o dono desta postagem.");
        }

        String urlNova = null;
        if (novaFoto != null && !novaFoto.isEmpty()) {
            urlNova = cloudinaryService.uploadImagem(novaFoto);
            request.setFoto(urlNova);
        }

        Servico servicoParaEditar = postagem.getServico();
        servicoService.editarServico(
                servicoParaEditar.getId(),
                request.getDescricaoPostagem(),
                request.getTipoServico(),
                request.getFoto()
        );

        postagemService.atualizarCamposPostagem(
                postagem.getId(),

                request.getTipoServico(),

                request.getDescricaoPostagem(),
                request.getFoto() // já atualizada se houve nova foto
        );
    }

    public List<Postagem> getPostagensPorCnpj(String cnpj) {
        return postagemService.getPostagemCnpj(cnpj);
    }

    public List<Postagem> getPostagens() {
        return postagemService.getPostagem();
    }

    public List<Postagem> getPostagensTipoServico(String tipoServico) {
        return postagemService.getPostagemPorTipo(tipoServico);
    }

    public void excluirPostagemServico(String emailLogado,
                                       Long idServico,
                                       Long idPostagem) {

        Usuario usuarioLogado = usuarioRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));

        Postagem postagem = postagemService.getPorId(idPostagem);

        if (!postagem.getPrestador().getPessoaJuridica().getCnpj()
                .equals(usuarioLogado.getDocumento())) {
            throw new RuntimeException("Acesso negado. Você não é o dono da postagem!");
        }

        postagemService.excluirPost(idPostagem);
        servicoService.excluirServ(idServico);
    }
}