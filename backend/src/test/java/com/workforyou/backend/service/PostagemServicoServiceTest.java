package com.workforyou.backend.service;


import com.workforyou.backend.dto.PostagemRequest;
import com.workforyou.backend.dto.PostagemResponse;
import com.workforyou.backend.dto.RegistroRequest;
import com.workforyou.backend.model.*;
import com.workforyou.backend.repository.PostagemRepository;
import com.workforyou.backend.repository.PrestadorRepository;
import com.workforyou.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.MediaType;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
    private PrestadorRepository prestadorRepository;

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
        String tipoServico = "Jardinagem";
        String descricao = "Corte de grama";
        String cnpj = "12345678000199";

        MultipartFile foto = mock(MultipartFile.class);

        when(foto.isEmpty()).thenReturn(false);
        when(cloudinaryService.uploadImagem(foto))
                .thenReturn("http://cloudinary.com/img123");

        Servico servico = new Servico();
        servico.setId(1L);

        when(servicoService.salvarServico(tipoServico, cnpj))
                .thenReturn(servico);

        PessoaJuridica pj = new PessoaJuridica();
        pj.setNome("Empresa X");

        Prestador prestador = new Prestador();
        prestador.setPessoaJuridica(pj);

        when(prestadorRepository.findByCnpj(cnpj))
                .thenReturn(Optional.of(prestador));

        when(postagemRepository.save(any(Postagem.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        postagemServicoService.salvarPostagemServico(tipoServico, descricao, cnpj, foto);

        verify(cloudinaryService).uploadImagem(foto);
        verify(servicoService).salvarServico(tipoServico, cnpj);
        verify(prestadorRepository).findByCnpj(cnpj);
        verify(postagemRepository).save(any(Postagem.class));
    }

    @Test
    public void testaCriarPostagemNegativo(){
        String tipoServico = "Jardinagem";
        String descricao = "Corte de grama";
        String cnpj = "00000000000000";
        MultipartFile foto = mock(MultipartFile.class);

        when(foto.isEmpty()).thenReturn(true);
        when(servicoService.salvarServico(tipoServico, cnpj))
                .thenReturn(new Servico());

        when(prestadorRepository.findByCnpj(cnpj))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> postagemServicoService.salvarPostagemServico(tipoServico, descricao, cnpj, foto)
        );

        assertEquals("Prestador não encontrado para o CNPJ: " + cnpj, ex.getMessage());

        verify(postagemRepository, never()).save(any());
        verify(cloudinaryService, never()).uploadImagem(any());
    }

    @Test
    public void testaToResponse(){
        PessoaJuridica pj = new PessoaJuridica();
        pj.setTelefone("11999999999");

        Prestador prestador = new Prestador();
        prestador.setId(10L);
        prestador.setEmail("prestador@mail.com");
        prestador.setUrlFoto("foto-perfil.jpg");
        prestador.setPessoaJuridica(pj);

        Postagem postagem = new Postagem();
        postagem.setId(1L);
        postagem.setTipoServico("Conserto de PC");
        postagem.setDescricaoPostagem("Formatação completa e limpeza interna");
        postagem.setUrlFoto("foto-postagem.jpg");
        postagem.setPrestador(prestador);
        postagem.setNomePrestador("Tech Informática");
        postagem.setCnpj("12345678000199");

        PostagemServicoService service = new PostagemServicoService();

        PostagemResponse response = service.toResponse(postagem);

        assertNotNull(response);

        assertEquals(1L, response.getId());
        assertEquals("Conserto de PC", response.getTipoServico());
        assertEquals("Formatação completa e limpeza interna", response.getDescricaoPostagem());
        assertEquals("foto-postagem.jpg", response.getFoto());

        assertNotNull(response.getPrestador());
        assertEquals(10L, response.getPrestador().getId());
        assertEquals("Tech Informática", response.getPrestador().getNome());
        assertEquals("prestador@mail.com", response.getPrestador().getEmail());
        assertEquals("12345678000199", response.getPrestador().getCnpj());
        assertEquals("foto-perfil.jpg", response.getPrestador().getFoto());

        assertEquals("11999999999", response.getTelefone());
        assertEquals("prestador@mail.com", response.getEmail());
    }

    @Test
    public void testaToResponseNegativo(){
        Prestador prestador = new Prestador();
        prestador.setId(20L);
        prestador.setEmail("teste@mail.com");
        prestador.setUrlFoto("foto.jpg");

        Postagem postagem = new Postagem();
        postagem.setId(2L);
        postagem.setTipoServico("Pintura");
        postagem.setDescricaoPostagem("Pintura interna");
        postagem.setUrlFoto("foto-pintura.jpg");
        postagem.setPrestador(prestador);
        postagem.setNomePrestador("João Pintor");
        postagem.setCnpj("99999999000111");

        PostagemServicoService service = new PostagemServicoService();

        PostagemResponse response = service.toResponse(postagem);

        assertNotNull(response);

        assertNull(response.getTelefone());

        assertEquals("teste@mail.com", response.getEmail());
        assertEquals(20L, response.getPrestador().getId());
        assertEquals("João Pintor", response.getPrestador().getNome());
    }

    @Test
    public void testaEditarPostagemServico(){
        String emailLogado = "dono@teste.com";

        Usuario usuario = new Usuario();
        usuario.setDocumento("12345678000199");
        when(usuarioRepository.findByEmail(emailLogado))
                .thenReturn(Optional.of(usuario));

        PessoaJuridica pj = new PessoaJuridica();
        pj.setCnpj("12345678000199");

        Prestador prestador = new Prestador();
        prestador.setPessoaJuridica(pj);

        Postagem postagem = new Postagem();
        postagem.setId(10L);
        postagem.setPrestador(prestador);

        Servico servico = new Servico();
        servico.setId(40L);
        postagem.setServico(servico);

        when(postagemService.getPorId(10L))
                .thenReturn(postagem);

        PostagemRequest request = new PostagemRequest();
        request.setDescricaoPostagem("Nova descrição");
        request.setTipoServico("Jardinagem");

        MultipartFile novaFoto = mock(MultipartFile.class);
        when(novaFoto.isEmpty()).thenReturn(false);
        when(cloudinaryService.uploadImagem(novaFoto))
                .thenReturn("http://foto-nova.com/foto.png");

        postagemServicoService.editarPostagemServico(
                emailLogado,
                10L,
                request,
                novaFoto
        );

        verify(usuarioRepository).findByEmail(emailLogado);
        verify(postagemService).getPorId(10L);
        verify(cloudinaryService).uploadImagem(novaFoto);
        verify(servicoService).editarServico(
                eq(40L),
                eq("Nova descrição"),
                eq("Jardinagem"),
                eq("http://foto-nova.com/foto.png")
        );
        verify(postagemService).atualizarCamposPostagem(
                eq(10L),
                eq("Jardinagem"),
                eq("Nova descrição"),
                eq("http://foto-nova.com/foto.png")
        );
    }

    @Test
    public void testaEditarPostagemServicoNegativo(){
        String emailLogado = "nao_dono@teste.com";

        Usuario usuario = new Usuario();
        usuario.setDocumento("11111111000199");
        when(usuarioRepository.findByEmail(emailLogado))
                .thenReturn(Optional.of(usuario));

        PessoaJuridica pj = new PessoaJuridica();
        pj.setCnpj("22222222000199");

        Prestador prestador = new Prestador();
        prestador.setPessoaJuridica(pj);

        Postagem postagem = new Postagem();
        postagem.setId(10L);
        postagem.setPrestador(prestador);

        when(postagemService.getPorId(10L))
                .thenReturn(postagem);

        PostagemRequest request = new PostagemRequest();
        MultipartFile file = mock(MultipartFile.class);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> postagemServicoService.editarPostagemServico(
                        emailLogado,
                        10L,
                        request,
                        file
                )
        );

        assertEquals("Acesso negado. Você não é o dono desta postagem.", ex.getMessage());

        verify(cloudinaryService, never()).uploadImagem(any());
        verify(servicoService, never()).editarServico(any(), any(), any(), any());
        verify(postagemService, never()).atualizarCamposPostagem(any(), any(), any(), any());
    }

    @Test
    public void testaExcluirPostagemServico(){
        String emailLogado = "dono@mail.com";
        Long idServico = 5L;
        Long idPostagem = 10L;

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
