package com.workforyou.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workforyou.backend.dto.PostagemRequest;
import com.workforyou.backend.dto.PostagemResponse;
import com.workforyou.backend.model.*;
import com.workforyou.backend.repository.UsuarioRepository;
import com.workforyou.backend.service.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@AutoConfigureMockMvc(addFilters = false)
public class PostagemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    @Autowired
    private PostagemService postagemService;

    @MockBean
    private PostagemService postagemServiceMock;

    @Autowired
    private PostagemController postagemController;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ServicoService servicoService;

    @MockBean
    private PostagemServicoService postagemServicoService;

    private static final String API_PATH = "/postagem";

    @BeforeEach
    void setUp(){

    }

    @Test
    public void testaRegisterCliente() throws Exception {
        Principal principal = () -> "usuario@teste.com";

        PostagemRequest request = new PostagemRequest();
        request.setTipoServico("Serviço X");
        request.setDescricaoPostagem("Descrição do serviço");

        Usuario usuario = new Usuario();
        usuario.setDocumento("12345678000100");
        when(usuarioService.getUsuarioPorEmail(principal.getName())).thenReturn(usuario);

        doNothing().when(postagemServicoService)
                .salvarPostagemServico(anyString(), anyString(), anyString(), any(MultipartFile.class));

        MockMultipartFile dadosJson = new MockMultipartFile(
                "dados",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(request)
        );

        // MockMultipartFile para arquivo
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "arquivo.txt",
                "text/plain",
                "conteudo".getBytes()
        );

        mockMvc.perform(multipart(API_PATH + "/register")
                        .file(dadosJson)
                        .file(file)
                        .principal(principal))
                .andExpect(status().isCreated())
                .andExpect(content().string("Postagem criada!"));
    }

    @Test
    public void testaRegistrarClienteNegativo() throws Exception{
        PostagemRequest request = new PostagemRequest();
        request.setTipoServico("Serviço X");
        request.setDescricaoPostagem("Descrição do serviço");

        MockMultipartFile dadosJson = new MockMultipartFile(
                "dados",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(request)
        );

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "arquivo.txt",
                "text/plain",
                "conteudo".getBytes()
        );

        mockMvc.perform(multipart(API_PATH + "/register")
                        .file(dadosJson)
                        .file(file))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Usuário não autenticado."));

    }

    @Test
    public void testaGetPorId() throws Exception {
        Long idPostagem = 1L;

        Postagem postagem = new Postagem();
        postagem.setId(idPostagem);
        postagem.setDescricaoPostagem("Descrição de teste");

        PostagemResponse response = new PostagemResponse();
        response.setId(idPostagem);
        response.setDescricaoPostagem("Descrição de teste");

        when(postagemServicoService.getPorId(idPostagem)).thenReturn(postagem);
        when(postagemServicoService.toResponse(postagem)).thenReturn(response);

        mockMvc.perform(get(API_PATH + "/{id}", idPostagem)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    public void testaGetPorIdNegativo() throws Exception {
        Long idPostagem = 999L;

        when(postagemServicoService.getPorId(idPostagem))
                .thenThrow(new RuntimeException("ID não encontrado"));

        mockMvc.perform(get(API_PATH + "/{id}", idPostagem)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Postagem não encontrada: ID não encontrado"));

    }

    @Test
    public void testaGetCnpj() throws Exception {

        String cnpj = "12345678000100";
        Prestador prestador = new Prestador();
        prestador.setId(1L);

        Servico servico = new Servico();
        servico.setId(1L);

        Postagem postagem1 = new Postagem();
        postagem1.setId(1L);
        postagem1.setPrestador(prestador);
        postagem1.setServico(servico);
        postagem1.setDescricaoPostagem("Descrição 1");
        postagem1.setUrlFoto("foto1.jpg");

        Postagem postagem2 = new Postagem();
        postagem2.setId(2L);
        postagem2.setPrestador(prestador);
        postagem2.setServico(servico);
        postagem2.setDescricaoPostagem("Descrição 2");
        postagem2.setUrlFoto("foto2.jpg");

        List<Postagem> postagensMock = List.of(postagem1, postagem2);

        when(postagemServicoService.getPostagensPorCnpj(cnpj)).thenReturn(postagensMock);


        ResponseEntity<?> response = postagemController.getPorCnpj(cnpj);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(postagensMock, response.getBody());
        verify(postagemServicoService).getPostagensPorCnpj(cnpj);
    }

    @Test
    public void testaGetCnpjNegativoTipoServicoNaoExiste(){
        String cnpj = "12345678000100";

        doThrow(new RuntimeException("Erro ao buscar postagens"))
                .when(postagemServicoService).getPostagensPorCnpj(cnpj);

        ResponseEntity<?> response = postagemController.getPorCnpj(cnpj);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Erro ao buscar postagens", response.getBody());
    }

    @Test
    public void testaGetPostagens() throws Exception {
        List<Postagem> postagensMock = new ArrayList<>();
        postagensMock.add(new Postagem());
        postagensMock.add(new Postagem());

        when(postagemServicoService.getPostagens()).thenReturn(postagensMock);

        mockMvc.perform(get(API_PATH + "/getAll")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(postagensMock.size()));
    }

    @Test
    public void testaGetTipoServico(){
        String tipoServico = "Eletrica";

        Prestador prestador = new Prestador();
        prestador.setId(1L);

        Servico servico = new Servico();
        servico.setId(1L);

        Postagem postagem = new Postagem();
        postagem.setId(1L);
        postagem.setPrestador(prestador);
        postagem.setServico(servico);
        postagem.setDescricaoPostagem("Conserto de fios");
        postagem.setUrlFoto("foto.jpg");

        List<Postagem> postagensMock = List.of(postagem);

        when(postagemServicoService.getPostagensTipoServico(tipoServico)).thenReturn(postagensMock);


        ResponseEntity<?> response = postagemController.getPorTipoServico(tipoServico);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(postagensMock, response.getBody());
        verify(postagemServicoService).getPostagensTipoServico(tipoServico);
    }

    @Test
    public void testaDeletarPostagem() throws Exception {
        Principal principal = () -> "usuario@teste.com";

        mockMvc.perform(delete("/postagem/delete")
                        .param("idServico", "1")
                        .param("idPostagem", "2")
                        .principal(principal))
                .andExpect(status().isNoContent()); // 204

        verify(postagemServicoService, times(1))
                .excluirPostagemServico("usuario@teste.com", 1L, 2L);
    }

    @Test
    public void testaDeletarPostagemNegativo() throws Exception {
        Principal principal = () -> "usuario@teste.com";

        doThrow(new RuntimeException("Falha ao deletar postagem"))
                .when(postagemServicoService)
                .excluirPostagemServico(anyString(), anyLong(), anyLong());

        mockMvc.perform(delete("/postagem/delete")
                        .param("idServico", "1")
                        .param("idPostagem", "2")
                        .principal(principal))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Erro de deleção: Falha ao deletar postagem")));
    }

    @Test
    public void testaEditarPostagem() throws Exception {
        Principal principal = () -> "usuario@teste.com";
        Long idPostagem = 1L;

        PostagemRequest request = new PostagemRequest();
        request.setTipoServico("Serviço X");
        request.setDescricaoPostagem("Descrição atualizada");

        MockMultipartFile dadosJson = new MockMultipartFile(
                "dados",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(request)
        );

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "arquivo.txt",
                "text/plain",
                "conteudo".getBytes()
        );

        doNothing().when(postagemServicoService)
                .editarPostagemServico(anyString(), eq(idPostagem), any(PostagemRequest.class), any(MultipartFile.class));

        mockMvc.perform(multipart(API_PATH + "/edit/{idPostagem}", idPostagem)
                        .file(dadosJson)
                        .file(file)
                        .with(req -> { req.setMethod("PUT"); return req; })
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(content().string("Postagem e Serviço atualizados com sucesso!"));
    }

    @Test
    public void testaEditarPostagemNegativo() throws Exception {
        Long idPostagem = 1L;

        PostagemRequest requestBody = new PostagemRequest();
        requestBody.setTipoServico("Serviço X");
        requestBody.setDescricaoPostagem("Descrição atualizada");


        MockMultipartFile dadosJson = new MockMultipartFile(
                "dados",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(requestBody)
        );


        MockMultipartFile file = new MockMultipartFile(
                "file",
                "arquivo.txt",
                "text/plain",
                "conteudo".getBytes()
        );

        mockMvc.perform(multipart(API_PATH + "/edit/{idPostagem}", idPostagem)
                        .file(dadosJson)
                        .file(file)
                        .with(req -> { req.setMethod("PUT"); return req; }))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Usuário não autenticado."));
    }

}

