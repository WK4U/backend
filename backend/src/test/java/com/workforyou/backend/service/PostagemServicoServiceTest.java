package com.workforyou.backend.service;


import com.workforyou.backend.dto.PostagemRequest;
import com.workforyou.backend.dto.RegistroRequest;
import com.workforyou.backend.model.*;
import com.workforyou.backend.repository.PostagemRepository;
import com.workforyou.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.MediaType;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PostagemServicoServiceTest {

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private ServicoService servicoService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PostagemService postagemService;

    @Mock
    private PostagemRepository postagemRepository;

    @InjectMocks
    private PostagemServicoService postagemServicoService;

    @Test
    public void criarPostagemTeste(){
        String nomeServico = "Manutenção Elétrica";
        String tipoServico = "Serviço Técnico";
        String descricaoS = "Serviço de manutenção elétrica residencial";
        String descricaoP = "Promoção de 20% em serviços residenciais";
        String cnpj = "12345678000100";

        when(multipartFile.isEmpty()).thenReturn(false);

        String url = "http//fotoMentira/img.jpg";
        when(cloudinaryService.uploadImagem(multipartFile)).thenReturn(url);

        Servico servico = new Servico();
        servico.setId(1L);

        when(servicoService.salvarServico(nomeServico, tipoServico, descricaoS, cnpj))
                .thenReturn(servico);


        postagemServicoService.salvarPostagemServico(nomeServico, tipoServico, descricaoS, descricaoP, cnpj, multipartFile);


        verify(servicoService, times(1)).salvarServico(nomeServico, tipoServico, descricaoS, cnpj);
        verify(cloudinaryService).uploadImagem(multipartFile);
       // verify(postagemService, times(1)).salvarNovaPostagem(url, descricaoP, cnpj, servico.getId());
    }

    @Test
    public void testaCriarPostagemNegativo(){
        String nomeServico = "Serviço X";
        String tipoServico = "Tipo Y";
        String descricaoS = "Descrição S";
        String descricaoP = "Descrição P";
        String cnpj = "12345678000100";

        when(multipartFile.isEmpty()).thenReturn(false);

        when(cloudinaryService.uploadImagem(multipartFile)).thenReturn("urlFalsa");

        when(servicoService.salvarServico(nomeServico, tipoServico, descricaoS, cnpj))
                .thenReturn(null);


        assertThrows(NullPointerException.class, () -> {
            postagemServicoService.salvarPostagemServico(nomeServico, tipoServico, descricaoS, descricaoP, cnpj, multipartFile);
        });

        verify(servicoService, times(1))
                .salvarServico(nomeServico, tipoServico, descricaoS, cnpj);

//        verify(postagemService, never())
//                .salvarNovaPostagem(anyString(), anyString(), anyString(), anyLong());
    }

    @Test
    public void testaEditarPostagemServico(){
        String emailLogado = "teste@exemplo.com";
        Long idPostagem = 1L;

        Usuario usuarioLogado = new Usuario();
        usuarioLogado.setEmail(emailLogado);
        usuarioLogado.setDocumento("1234567890001");

        PessoaJuridica pessoaJuridica = new PessoaJuridica();
        pessoaJuridica.setCnpj("1234567890001");

        Prestador prestador = new Prestador();
        prestador.setPessoaJuridica(pessoaJuridica);

        Servico servico = new Servico();
        servico.setId(10L);
        servico.setNomeServico("Serviço antigo");

        Postagem postagem = new Postagem();
        postagem.setId(idPostagem);
        postagem.setPrestador(prestador);
        postagem.setServico(servico);

        PostagemRequest request = new PostagemRequest();
        request.setNomeServico("Serviço atualizado");
        request.setTipoServico("Limpeza");
        request.setDescricaoServico("Limpeza residencial completa");

        when(multipartFile.isEmpty()).thenReturn(false);
        when(usuarioRepository.findByEmail(emailLogado)).thenReturn(Optional.of(usuarioLogado));
        when(postagemService.getPorId(idPostagem)).thenReturn(postagem);

        postagemServicoService.editarPostagemServico(emailLogado, idPostagem, request, multipartFile);

        verify(servicoService, times(1)).editarServico(
                servico.getId(),
                "Serviço atualizado",
                "Limpeza",
                "Limpeza residencial completa");
    }

    @Test
    public void testaEditarPostagemServicoNegativo(){
        String emailLogado = "usuario@teste.com";
        Long idPostagem = 2L;

        Usuario usuarioLogado = new Usuario();
        usuarioLogado.setEmail(emailLogado);
        usuarioLogado.setDocumento("9999999990001");

        PessoaJuridica pessoaJuridica = new PessoaJuridica();
        pessoaJuridica.setCnpj("1234567890001");

        Prestador prestador = new Prestador();
        prestador.setPessoaJuridica(pessoaJuridica);

        Servico servico = new Servico();
        servico.setId(20L);
        servico.setNomeServico("Serviço qualquer");

        Postagem postagem = new Postagem();
        postagem.setId(idPostagem);
        postagem.setPrestador(prestador);
        postagem.setServico(servico);

        when(usuarioRepository.findByEmail(emailLogado)).thenReturn(Optional.of(usuarioLogado));
        when(postagemService.getPorId(idPostagem)).thenReturn(postagem);

        PostagemRequest request = new PostagemRequest();
        request.setNomeServico("Tentativa de edição");


        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                postagemServicoService.editarPostagemServico(emailLogado, idPostagem, request, multipartFile)
        );

        assertEquals("Acesso negado. Você não é o dono desta postagem.", ex.getMessage());
        verify(usuarioRepository, times(2)).findByEmail(emailLogado);
        verify(postagemService, times(1)).getPorId(idPostagem);
        verify(servicoService, never()).editarServico(anyLong(), anyString(), anyString(), anyString());
        verify(postagemService, never()).editarPostagem(anyLong(), anyString(), anyString());
        verify(cloudinaryService, never()).uploadImagem(any());
    }

    @Test
    public void testaExcluirPostagemServico(){
        String emailLogado = "teste@empresa.com";
        Long idServico = 1L;
        Long idPostagem = 2L;

        Usuario usuario = new Usuario();
        usuario.setDocumento("12345678000199");

        PessoaJuridica pj = new PessoaJuridica();
        pj.setCnpj("12345678000199");

        Prestador prestador = new Prestador();
        prestador.setPessoaJuridica(pj);

        Postagem postagem = new Postagem();
        postagem.setPrestador(prestador);

        when(usuarioRepository.findByEmail(emailLogado)).thenReturn(Optional.of(usuario));
        when(postagemService.getPorId(idPostagem)).thenReturn(postagem);

        postagemServicoService.excluirPostagemServico(emailLogado, idServico, idPostagem);

        verify(usuarioRepository, times(2)).findByEmail(emailLogado);
        verify(postagemService, times(1)).getPorId(idPostagem);
        verify(postagemService, times(1)).excluirPost(idPostagem);
        verify(servicoService, times(1)).excluirServ(idServico);
    }

    @Test
    public void testaExcluirPostagemServicoNegativo(){
        String emailLogado = "inexistente@teste.com";
        Long idServico = 1L;
        Long idPostagem = 2L;

        when(usuarioRepository.findByEmail(emailLogado)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postagemServicoService.excluirPostagemServico(emailLogado, idServico, idPostagem);
        });

        assertEquals("Usuário não encontrado!", exception.getMessage());
        verify(postagemService, never()).getPorId(anyLong());
        verify(postagemService, never()).excluirPost(anyLong());
        verify(servicoService, never()).excluirServ(anyLong());
    }



}
