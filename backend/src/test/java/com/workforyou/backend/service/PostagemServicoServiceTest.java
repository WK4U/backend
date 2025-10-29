package com.workforyou.backend.service;


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
import org.springframework.scheduling.support.SimpleTriggerContext;

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
        String foto = "foto.jpg";

        Servico servico = new Servico();
        servico.setId(1L);

        when(servicoService.salvarServico(nomeServico, tipoServico, descricaoS, cnpj))
                .thenReturn(servico);


        postagemServicoService.salvarPostagemServico(nomeServico, tipoServico, descricaoS, descricaoP, cnpj, foto);


        verify(servicoService, times(1))
                .salvarServico(nomeServico, tipoServico, descricaoS, cnpj);

        verify(postagemService, times(1))
                .salvarNovaPostagem(foto, descricaoP, cnpj, servico.getId());
    }

    @Test
    public void testaCriarPostagemNegativo(){
        String nomeServico = "Serviço X";
        String tipoServico = "Tipo Y";
        String descricaoS = "Descrição S";
        String descricaoP = "Descrição P";
        String cnpj = "12345678000100";
        String foto = "foto.png";

        when(servicoService.salvarServico(nomeServico, tipoServico, descricaoS, cnpj))
                .thenReturn(null);


        assertThrows(NullPointerException.class, () -> {
            postagemServicoService.salvarPostagemServico(nomeServico, tipoServico, descricaoS, descricaoP, cnpj, foto);
        });

        verify(servicoService, times(1))
                .salvarServico(nomeServico, tipoServico, descricaoS, cnpj);

        verify(postagemService, never())
                .salvarNovaPostagem(anyString(), anyString(), anyString(), anyLong());
    }

    @Test
    public void testaEditarPostagemServico(){
        Long idServico = 1L;
        String nomeServico = "Serviço Elétrico";
        String tipoServico = "Residencial";
        String descricaoServico = "Troca de fiação antiga";
        String descricaoPostagem = "Atualizamos o serviço com fotos novas!";
        String foto = "foto_atualizada.jpg";

        Servico servicoEditado = new Servico();
        servicoEditado.setId(idServico);

        when(servicoService.editarServico(idServico, nomeServico, tipoServico, descricaoServico))
                .thenReturn(servicoEditado);


        postagemServicoService.editarPostagemServico(idServico, nomeServico, tipoServico, descricaoServico, descricaoPostagem, foto);


        verify(servicoService, times(1))
                .editarServico(idServico, nomeServico, tipoServico, descricaoServico);

        verify(postagemService, times(1))
                .editarPostagem(servicoEditado.getId(), foto, descricaoPostagem);
    }

    @Test
    public void testaEditarPostagemServicoNegativo(){
        Long idServico = 1L;
        String nomeServico = "Serviço X";
        String tipoServico = "Tipo Y";
        String descricaoServico = "Descrição S";
        String descricaoPostagem = "Descrição P";
        String foto = "foto.png";

        when(servicoService.editarServico(idServico, nomeServico, tipoServico, descricaoServico))
                .thenReturn(null);


        assertThrows(NullPointerException.class, () -> {
            postagemServicoService.editarPostagemServico(idServico, nomeServico, tipoServico, descricaoServico, descricaoPostagem, foto);
        });

        verify(servicoService, times(1))
                .editarServico(idServico, nomeServico, tipoServico, descricaoServico);

        verify(postagemService, never()).editarPostagem(anyLong(), anyString(), anyString());
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
