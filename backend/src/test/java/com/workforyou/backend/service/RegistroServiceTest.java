package com.workforyou.backend.service;

import com.workforyou.backend.dto.RegistroRequest;
import com.workforyou.backend.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistroServiceTest {

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private PasswordEncoder  passwordEncoder;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private FisicaService fisicaService;

    @Mock
    private PrestadorService prestadorService;

    @Mock
    private ClienteService clienteService;

    @Mock
    private JuridicaService juridicaService;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private RegistroService registroService;

    @Test
    public void salvarNovoUsuarioTeste(){
        RegistroRequest request = new RegistroRequest();
        request.setEmail("pessoa@gmail.com");
        request.setTipoUsuario("FISICO");
        request.setCpf("12345678900");

        PessoaFisica mockPessoaFisica = new PessoaFisica();
        mockPessoaFisica.setCpf("12345678900");

        when(usuarioService.verificarEmailExistente(request.getEmail())).thenReturn(false);

        when(multipartFile.isEmpty()).thenReturn(false);

        when(cloudinaryService.uploadImagem(multipartFile)).thenReturn("UrlDaFoto");

        when(fisicaService.verificarCpf(any())).thenReturn(true);

        when(fisicaService.criarPessoaFisica(request)).thenReturn(mockPessoaFisica);

        registroService.salvarNovoUsuario(request, multipartFile);

        verify(usuarioService, times(1)).verificarEmailExistente("pessoa@gmail.com");
        verify(multipartFile, times(1)).isEmpty();
        verify(cloudinaryService, times(1)).uploadImagem(multipartFile);
        verify(fisicaService, times(1)).verificarCpf("12345678900");
        verify(clienteService, times(1)).criarCliente(eq(request), any());
        verify(clienteService, times(1)).criarCliente(request, mockPessoaFisica);

    }

    @Test
    public void testaSalvarNovoUsuarioNegativo(){

        RegistroRequest request = new RegistroRequest();
        request.setEmail("pessoa@gmail.com");
        request.setTipoUsuario("FISICO");
        request.setCpf("12345678900");

        when(usuarioService.verificarEmailExistente(request.getEmail())).thenReturn(true);

        RuntimeException ex =  assertThrows(RuntimeException.class, () ->{
            registroService.salvarNovoUsuario(request, null);
        });

        assertEquals("Este e-mail já está em uso.", ex.getMessage());

        verify(usuarioService, times(1)).verificarEmailExistente("pessoa@gmail.com");
        verifyNoInteractions(fisicaService);
        verifyNoInteractions(juridicaService);
        verifyNoInteractions(clienteService);
        verifyNoInteractions(cloudinaryService);
    }

    @Test
    public void salvarUsuarioFisicoTeste(){

        RegistroRequest request = new RegistroRequest();
        request.setEmail("teste@email.com");
        request.setSenha("123456");
        request.setTipoUsuario("FISICO");
        request.setCpf("12345678900");

        PessoaFisica fisica = new PessoaFisica();
        fisica.setCpf("12345678900");

        when(multipartFile.isEmpty()).thenReturn(false);
        when(cloudinaryService.uploadImagem(multipartFile)).thenReturn("UrlDaFoto");
        when(usuarioService.verificarEmailExistente(request.getEmail())).thenReturn(false);
        when(fisicaService.verificarCpf(request.getCpf())).thenReturn(true);
        when(fisicaService.criarPessoaFisica(request)).thenReturn(fisica);

        registroService.salvarNovoUsuario(request, multipartFile);

        verify(fisicaService, times(1)).verificarCpf("12345678900");
        verify(fisicaService, times(1)).criarPessoaFisica(request);
        verify(multipartFile, times(1)).isEmpty();
        verify(cloudinaryService, times(1)).uploadImagem(multipartFile);
        verify(clienteService, times(1)).criarCliente(request, fisica);
        verify(usuarioService, times(1)).criarUsuario("teste@email.com", "123456", 'f', "12345678900");
    }

    @Test
    public void testaSalvarFisicoNegativo(){
        RegistroRequest request = new RegistroRequest();
        request.setEmail("teste@email.com");
        request.setSenha("123456");
        request.setTipoUsuario("FISICO");
        request.setCpf("12345678900");

        when(usuarioService.verificarEmailExistente(request.getEmail())).thenReturn(false);
        when(fisicaService.verificarCpf(request.getCpf())).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            registroService.salvarNovoUsuario(request, null);
        });

        assertEquals("CPF JÁ CADASTRADO!", exception.getMessage());

        verify(fisicaService, times(1)).verificarCpf("12345678900");
        verify(fisicaService, never()).criarPessoaFisica(any());
        verify(clienteService, never()).criarCliente(any(), any());
        verify(usuarioService, never()).criarUsuario(anyString(), anyString(), anyChar(), anyString());
        verifyNoInteractions(cloudinaryService);
    }

    @Test
    public void testaSalvarJuridico(){
        RegistroRequest request = new RegistroRequest();
        request.setEmail("teste@email.com");
        request.setSenha("123456");
        request.setTipoUsuario("JURIDICO");
        request.setCnpj("12453635658799");

        PessoaJuridica juridica = new PessoaJuridica();
        juridica.setCnpj("12453635658799");

        when(multipartFile.isEmpty()).thenReturn(false);
        when(cloudinaryService.uploadImagem(multipartFile)).thenReturn("UrlDaFoto");
        when(usuarioService.verificarEmailExistente(request.getEmail())).thenReturn(false);
        when(juridicaService.verificarCnpj(request.getCnpj())).thenReturn(true);
        when(juridicaService.criarPessoaJuridica(request)).thenReturn(juridica);

        registroService.salvarNovoUsuario(request, multipartFile);

        verify(juridicaService, times(1)).verificarCnpj("12453635658799");
        verify(multipartFile, times(1)).isEmpty();
        verify(cloudinaryService, times(1)).uploadImagem(multipartFile);
        verify(juridicaService, times(1)).criarPessoaJuridica(request);
        verify(prestadorService, times(1)).criarPrestador(request, juridica);
        verify(usuarioService, times(1)).criarUsuario("teste@email.com", "123456", 'j', "12453635658799");
    }

    @Test
    public void testaSlvarJuridicoNegativo(){
        RegistroRequest request = new RegistroRequest();
        request.setEmail("teste@email.com");
        request.setSenha("123456");
        request.setTipoUsuario("JURIDICO");
        request.setCnpj("12453635658799");

        when(juridicaService.verificarCnpj(request.getCnpj())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            registroService.salvarNovoUsuario(request, null);
        });

        assertEquals("CNPJ JÁ CADASTRADO!", ex.getMessage());

        verify(juridicaService, never()).criarPessoaJuridica(any());
        verify(prestadorService, never()).criarPrestador(any(), any());
        verify(usuarioService, never()).criarUsuario(anyString(), anyString(), anyChar(), anyString());
        verifyNoInteractions(cloudinaryService);
    }

    @Test
    public void testaEditarUsuario(){
        String email = "teste@email.com";

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setSenha("senhaCriptografada");
        usuario.setTipoUsuario('f');

        RegistroRequest request = new RegistroRequest();
        request.setSenha("123456");

        MultipartFile file = Mockito.mock(MultipartFile.class);

        Cliente cliente = new Cliente();
        PessoaFisica pf = new PessoaFisica();
        cliente.setPessoaFisica(pf);

        when(usuarioService.getUsuarioPorEmail(email)).thenReturn(usuario);
        when(passwordEncoder.matches("123456", "senhaCriptografada")).thenReturn(true);

        when(file.isEmpty()).thenReturn(false);
        when(cloudinaryService.uploadImagem(file)).thenReturn("url_foto");

        when(clienteService.getClientePorEmail(email)).thenReturn(cliente);

        registroService.editarUsuario(email, request, file);

        assertEquals("url_foto", request.getUriFoto());
        verify(fisicaService).editarPessoaFisica(pf, request);
        verify(clienteService).editarCliente(cliente, request);
    }

    @Test
    public void testaEditarUsuarioSenhaIncorreta(){
        String email = "email@test.com";

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setSenha("hash");
        usuario.setTipoUsuario('f');

        RegistroRequest request = new RegistroRequest();
        request.setSenha("senhaerrada");

        when(usuarioService.getUsuarioPorEmail(email)).thenReturn(usuario);
        when(passwordEncoder.matches("senhaerrada", "hash")).thenReturn(false);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> registroService.editarUsuario(email, request, null)
        );

        assertEquals("Senha incorreta! Alterações não permitidas.", ex.getMessage());
    }
}