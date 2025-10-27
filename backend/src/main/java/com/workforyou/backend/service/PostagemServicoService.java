package com.workforyou.backend.service;

import com.workforyou.backend.model.Postagem;
import com.workforyou.backend.model.Servico;
import com.workforyou.backend.model.Usuario;
import com.workforyou.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostagemServicoService {

    @Autowired
    private ServicoService servicoService;

    @Autowired
    private PostagemService postagemService;

    @Autowired
    private UsuarioRepository usuarioRepository;

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
    public void excluirPostagemServico(String emailLogado,Long idServico, Long idPostagem){
        if(usuarioRepository.findByEmail(emailLogado).isEmpty()){
            throw new RuntimeException("Usuário não encontrado!");
        }
        Usuario usuarioLogado = usuarioRepository.findByEmail(emailLogado).get();

        Postagem postagem = postagemService.getPorId(idPostagem);

        if(!postagem.getPrestador().getPessoaJuridica().getCnpj().equals(usuarioLogado.getDocumento())){
            throw new RuntimeException("Acesso negado. Você não é o dono da postagem!");
        }

        postagemService.excluirPost(idPostagem);

        servicoService.excluirServ(idServico);
    }
}
