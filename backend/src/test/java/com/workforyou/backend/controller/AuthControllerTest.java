package com.workforyou.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.workforyou.backend.config.JwtUtil;
import com.workforyou.backend.dto.RegistroRequest;
import com.workforyou.backend.model.Usuario;
import com.workforyou.backend.repository.UsuarioRepository;
import com.workforyou.backend.service.RegistroService;
import com.workforyou.backend.service.UsuarioService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.sendgrid.SendGridAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MultipartFile multipartFile;

    @MockBean
    private RegistroService registroService;

    @MockBean
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private static final String API_PATH = "/auth";

    @BeforeEach
    void setUp(){

    }

    @Test
    public void testeIntegracaoRegisterCliente() throws Exception{
        String json = """
        {
            "nome": "Marcos",
            "telefone": "568486564",
            "cpf": "05764586659",
            "dataNascimento": "1990-02-02",
            "email": "pessoa@gmail.com",
            "senha": "4321",
            "tipoUsuario": "FISICO"
        }
    """;

        MockMultipartFile dados = new MockMultipartFile(
                "dados",
                "",
                "application/json",
                json.getBytes()
        );

        mockMvc.perform(multipart(API_PATH + "/register")
                        .file(dados)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().string("Conta registrada com sucesso!"));
    }

    @Test
    public void testeIntegracaoRegisterClienteNegativo() throws Exception{

        String json = """
        {
            "nome": "Carlos",
            "telefone": "123456789",
            "cpf": "11122233344",
            "dataNascimento": "1990-01-01",
            "email": "falha@gmail.com",
            "senha": "1234",
            "tipoUsuario": "FISICO"
        }
    """;

        MockMultipartFile dados = new MockMultipartFile(
                "dados",
                "",
                "application/json",
                json.getBytes()
        );

        doThrow(new RuntimeException("Erro ao salvar usuário"))
                .when(registroService).salvarNovoUsuario(any(), any());

        mockMvc.perform(multipart(API_PATH + "/register")
                        .file(dados)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isConflict())
                .andExpect(content().string("Erro ao salvar usuário"));
    }

    @Test
    public void testaLogin()throws Exception{
        Usuario usuario = new Usuario();
        usuario.setEmail("teste@exemplo.com");
        usuario.setSenha("1234");
        usuario.setTipoUsuario('f');
        usuario.setDocumento("25346767");
        usuarioRepository.save(usuario);


        mockMvc.perform(post(API_PATH + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "email": "teste@exemplo.com",
                    "senha": "1234"
                }
            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    public void testaLoginEmailInexistente()throws Exception{
        mockMvc.perform(post(API_PATH + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "email": "naoexiste@exemplo.com",
                                "senha": "qualquer"
                            }
                        """))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Não encontrado!"));
    }

    @Test
    public void testaEsqueceuSenha()throws Exception{
        mockMvc.perform(post(API_PATH + "/esqueceu-senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "email": "matheusranheri@gmail.com"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(content().string("Código de redefinição enviado por email"));
    }

    @Test
    public void testaEsqueceuSenhaEmailInvalidoNegativo()throws Exception{
        mockMvc.perform(post(API_PATH + "/esqueceu-senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "email": ""
                            }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("O e-mail é obrigatório"));
    }

    @Test
    public void testaValidarPin()throws Exception{
        String email = "usuario@teste.com";
        String pin = "123456";

        Map<String, String> requestBody = Map.of(
                "email", email,
                "pin", pin
        );

        when(usuarioService.validarPin(email, pin)).thenReturn(true);

        try (MockedStatic<JwtUtil> jwtMock = mockStatic(JwtUtil.class)) {
            jwtMock.when(() -> JwtUtil.gerarToken(email)).thenReturn("tokenSimulado123");

            mockMvc.perform(post("/auth/validar-pin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(requestBody)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tokenReset").value("tokenSimulado123"));
        }

        verify(usuarioService, times(1)).validarPin(email, pin);
    }

    @Test
    public void testaValidarPinNegativo() throws Exception{
        Usuario usuario = new Usuario();
        usuario.setEmail("teste@exemplo.com");
        usuario.setSenha("1234");
        usuario.setTipoUsuario('f');
        usuario.setDocumento("25346767");
        usuarioRepository.save(usuario);

        mockMvc.perform(post(API_PATH + "/validar-pin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"teste@exemplo.com\", \"pin\":\"0000\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("PIN inválido ou expirado"));
    }

    @Test
    public void testaRedefinirSenha() throws Exception {
        String code = "codigoValido123";
        String novaSenha = "NovaSenhaSegura@123";

        Map<String, String> requestBody = Map.of(
                "novaSenha", novaSenha,
                "code", code
        );

        when(usuarioService.atualizarSenhaComToken(code, novaSenha)).thenReturn(true);

        mockMvc.perform(patch("/auth/redefinir-senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(content().string("Senha redefinida com sucesso"));

        verify(usuarioService, times(1)).atualizarSenhaComToken(code, novaSenha);
    }

    @Test
    public void testeRedefinirSenhaSenhaVazia() throws Exception {
        String jsonRequest = """
        {
            "novaSenha": "",
            "code": "qualquerToken"
        }
    """;

        mockMvc.perform(patch(API_PATH + "/redefinir-senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Nova senha é obrigatoria"));
    }

    @Test
    void editarConta_positivo() throws Exception {
        String json = """
        {
            "nome": "Matheus",
            "telefone": "11999999999"
        }
    """;

        MockMultipartFile dados = new MockMultipartFile(
                "dados",
                "",
                "application/json",
                json.getBytes()
        );


        MockMultipartFile file = new MockMultipartFile(
                "file",
                "foto.png",
                "image/png",
                "fake image".getBytes()
        );


        Principal principal = () -> "teste@exemplo.com";


        doNothing().when(registroService)
                .editarUsuario(anyString(), any(RegistroRequest.class), any(MultipartFile.class));

        mockMvc.perform(multipart(API_PATH + "/edit")
                        .file(dados)
                        .file(file)
                        .principal(principal)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                )
                .andExpect(status().isOk())
                .andExpect(content().string("Perfil atualizado com sucesso!"));
    }

    @Test
    void editarContaNaoAutenticado() throws Exception {
        String json = """
            {
                "nome": "Matheus"
            }
            """;

        MockMultipartFile dados = new MockMultipartFile(
                "dados",
                "",
                "application/json",
                json.getBytes()
        );

        mockMvc.perform(multipart(API_PATH + "/edit")
                        .file(dados)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                )
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Usuário não autenticado."));
    }


}
