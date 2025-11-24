package com.workforyou.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workforyou.backend.dto.PostagemRequest;
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
        final String emailTeste = "email@teste.com";
        final String cnpjTeste = "12345678000199";

        Principal principal = () -> emailTeste;

        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail(emailTeste);
        usuarioMock.setDocumento(cnpjTeste);

        when(usuarioService.getUsuarioPorEmail(emailTeste))
                .thenReturn(usuarioMock);

        PostagemRequest requestObj = new PostagemRequest();
        requestObj.setNomeServico("Serviço Teste");
        requestObj.setTipoServico("Tipo A");
        requestObj.setDescricaoServico("Descrição do serviço");
        requestObj.setDescricaoPostagem("Descrição da postagem");
        requestObj.setCnpj(cnpjTeste);

        String requestJson = objectMapper.writeValueAsString(requestObj);

        MockMultipartFile dados = new MockMultipartFile(
                "dados",
                "dados.json",
                "application/json",
                requestJson.getBytes()
        );

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "foto_teste.png",
                MediaType.IMAGE_PNG_VALUE,
                "conteúdo da foto".getBytes()
        );

        doNothing().when(postagemServicoService)
                .salvarPostagemServico(any(), any(), any(), any(), any(), any());

        mockMvc.perform(MockMvcRequestBuilders.multipart(API_PATH + "/register")
                        .file(dados)
                        .file(file)
                        .principal(principal)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string("Postagem criada!"));

        verify(postagemServicoService, times(1))
                .salvarPostagemServico(
                        eq(requestObj.getNomeServico()),
                        eq(requestObj.getTipoServico()),
                        eq(requestObj.getDescricaoServico()),
                        eq(requestObj.getDescricaoPostagem()),
                        eq(cnpjTeste),
                        any(MultipartFile.class)
                );
    }

    @Test
    public void testaRegistrarClienteNegativo() throws Exception{
        final String emailTeste = "email@teste.com";

        Principal principal = () -> emailTeste;

        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail(emailTeste);
        usuarioMock.setDocumento(null);

        when(usuarioService.getUsuarioPorEmail(emailTeste))
                .thenReturn(usuarioMock);

        PostagemRequest req = new PostagemRequest();
        req.setNomeServico("Serviço Teste");
        req.setTipoServico("Tipo A");
        req.setDescricaoServico("Descrição do serviço");
        req.setDescricaoPostagem("Descrição da postagem");
        req.setCnpj(null); // obrigatório mas não preenchido

        String json = objectMapper.writeValueAsString(req);

        MockMultipartFile dados = new MockMultipartFile(
                "dados",
                "dados.json",
                MediaType.APPLICATION_JSON_VALUE,
                json.getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "foto.png",
                MediaType.IMAGE_PNG_VALUE,
                "AAAA".getBytes()
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart(API_PATH + "/register")
                        .file(dados)
                        .file(file)
                        .principal(principal)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(postagemServicoService, never())
                .salvarPostagemServico(any(), any(), any(), any(), any(), any());

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
        Long idPostagem = 1L;

        PostagemRequest request = new PostagemRequest();
        request.setNomeServico("Serviço Atualizado");
        request.setTipoServico("Elétrica");
        request.setDescricaoServico("Instalação de fiação revisada");
        request.setDescricaoPostagem("Atualização na postagem do serviço");

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "nova_foto.png",
                MediaType.IMAGE_PNG_VALUE,
                "conteúdo de teste".getBytes()
        );

        String requestJson = objectMapper.writeValueAsString(request);

        MockMultipartFile dados = new MockMultipartFile(
                "dados",
                null,
                MediaType.APPLICATION_JSON_VALUE,
                requestJson.getBytes()
        );

        doNothing().when(postagemServicoService)
                .editarPostagemServico(anyString(), eq(idPostagem), any(PostagemRequest.class), any(MultipartFile.class));


        mockMvc.perform(MockMvcRequestBuilders.multipart(API_PATH + "/edit/{idPostagem}", idPostagem)
                        .file(file)
                        .file(dados)
                        .with(req -> {
                            req.setMethod("PUT");
                            return req;
                        })
                        .principal(() -> "usuario@teste.com"))

                .andExpect(status().isOk())
                .andExpect(content().string("Postagem e Serviço atualizados com sucesso!"));


        verify(postagemServicoService, times(1))
                .editarPostagemServico(eq("usuario@teste.com"), eq(idPostagem), eq(request), eq(file));
    }

    @Test
    public void testaEditarPostagemNegativo() throws Exception {
        Long idPostagem = 3L;

        PostagemRequest request = new PostagemRequest();
        request.setNomeServico("Serviço Qualquer");
        request.setTipoServico("Geral");
        request.setDescricaoServico("Dados de teste");
        request.setDescricaoPostagem("Postagem sem principal");

        String requestJson = objectMapper.writeValueAsString(request);

        MockMultipartFile dados = new MockMultipartFile(
                "dados",
                null,
                MediaType.APPLICATION_JSON_VALUE,
                requestJson.getBytes()
        );

        MockMultipartFile fileVazio = new MockMultipartFile(
                "file",
                "",
                MediaType.TEXT_PLAIN_VALUE,
                new byte[0]
        );


        mockMvc.perform(MockMvcRequestBuilders.multipart(API_PATH + "/edit/{idPostagem}", idPostagem)
                        .file(dados)
                        .file(fileVazio)
                        .with(req -> {
                            req.setMethod("PUT");
                            return req;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))

                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Usuário não autenticado."));


        verify(postagemServicoService, times(0))
                .editarPostagemServico(anyString(), anyLong(), any(PostagemRequest.class), any(MultipartFile.class));
    }

}

