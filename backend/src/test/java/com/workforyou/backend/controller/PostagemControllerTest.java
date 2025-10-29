package com.workforyou.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workforyou.backend.dto.PostagemRequest;
import com.workforyou.backend.model.PessoaJuridica;
import com.workforyou.backend.model.Postagem;
import com.workforyou.backend.model.Prestador;
import com.workforyou.backend.model.Servico;
import com.workforyou.backend.service.PostagemService;
import com.workforyou.backend.service.PostagemServicoService;
import com.workforyou.backend.service.RegistroService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Autowired
    private PostagemService postagemService;

    @Autowired
    private PostagemController postagemController;

    @MockBean
    private PostagemServicoService postagemServicoService;

    private static final String API_PATH = "/postagem";

    @BeforeEach
    void setUp(){

    }

    @Test
    public void testaRegisterCliente() throws Exception {
        String jsonRequest = """
        {
            "nomeServico": "Serviço Teste",
            "tipoServico": "Tipo A",
            "descricaoServico": "Descrição do serviço",
            "descricaoPostagem": "Descrição da postagem",
            "cnpj": "12345678000199",
            "foto": "base64string"
        }
    """;

        mockMvc.perform(post(API_PATH + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(content().string("Postagem criada!"));
    }

    @Test
    public void testaRegistrarClienteNegativoCampoObrigatorio() throws Exception{
        String jsonRequest = """
    {
        "nomeServico": "Serviço Teste",
        "tipoServico": "Tipo Teste",
        "descricaoServico": "Descrição",
        "descricaoPostagem": "Descrição da postagem",
        "cnpj": "12345678000199",
        "foto": "foto.jpg"
    }
    """;

        doThrow(new RuntimeException("Erro ao salvar postagem"))
                .when(postagemServicoService)
                .salvarPostagemServico(any(), any(), any(), any(), any(), any());

        mockMvc.perform(post(API_PATH + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Erro ao salvar postagem"));

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

        mockMvc.perform(get(API_PATH + "/get")
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
        Long idServico = 1L;
        PostagemRequest request = new PostagemRequest();
        request.setNomeServico("Serviço Atualizado");
        request.setTipoServico("Limpeza");
        request.setDescricaoServico("Serviço de limpeza residencial");
        request.setDescricaoPostagem("Agora com desconto");
        request.setFoto("foto_atualizada.jpg");


        mockMvc.perform(put("/postagem/edit/{idServico}", idServico)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))

                .andExpect(status().isOk())
                .andExpect(content().string("Serviço e postagem editados!"));

        verify(postagemServicoService, times(1)).editarPostagemServico(
                eq(1L),
                eq("Serviço Atualizado"),
                eq("Limpeza"),
                eq("Serviço de limpeza residencial"),
                eq("Agora com desconto"),
                eq("foto_atualizada.jpg")
        );
    }

    @Test
    public void testaEditarPostagemNegativo() throws Exception {
        Long idServico = 1L;
        PostagemRequest request = new PostagemRequest();
        request.setNomeServico("Serviço com erro");
        request.setTipoServico("Pintura");
        request.setDescricaoServico("Serviço de pintura");
        request.setDescricaoPostagem("Erro simulado");
        request.setFoto("erro.jpg");

        doThrow(new RuntimeException("Falha ao editar")).when(postagemServicoService)
                .editarPostagemServico(anyLong(), anyString(), anyString(), anyString(), anyString(), anyString());

        mockMvc.perform(put("/postagem/edit/{idServico}", idServico)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))

                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Erro de edição:Falha ao editar")));

        verify(postagemServicoService, times(1)).editarPostagemServico(
                anyLong(), anyString(), anyString(), anyString(), anyString(), anyString());
    }

}
