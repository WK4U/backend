package com.workforyou.backend.service;

import com.workforyou.backend.dto.RegistroRequest;
import com.workforyou.backend.model.Cliente;
import com.workforyou.backend.model.PessoaFisica;
import com.workforyou.backend.model.PessoaJuridica;
import com.workforyou.backend.model.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
        final String email = "ana.fisica@test.com";

        Usuario usuarioFisico = new Usuario();
        usuarioFisico.setTipoUsuario('f');

        PessoaFisica pessoaFisica = new PessoaFisica();
        Cliente cliente = new Cliente();
        cliente.setPessoaFisica(pessoaFisica);

        RegistroRequest request = new RegistroRequest();
        request.setNome("Novo Nome");

        when(usuarioService.getUsuarioPorEmail(email)).thenReturn(usuarioFisico);
        when(clienteService.getClientePorEmail(email)).thenReturn(cliente);


        when(fisicaService.editarPessoaFisica(any(PessoaFisica.class), any(RegistroRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(clienteService.editarCliente(any(Cliente.class), any(RegistroRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        registroService.editarUsuario(email, request, null);

        verify(usuarioService, times(1)).getUsuarioPorEmail(email);
        verify(clienteService, times(1)).getClientePorEmail(email);

        verify(fisicaService, times(1)).editarPessoaFisica(pessoaFisica, request);
        verify(clienteService, times(1)).editarCliente(cliente, request);


        verify(prestadorService, never()).getPorEmail(anyString());
        verify(juridicaService, never()).editarPessoaJuridica(any(), any());
        verify(prestadorService, never()).editarPrestador(any(),any());
        verify(cloudinaryService, never()).uploadImagem(any());
    }

    @Test
    public void testaEditarUsuarioComUsuarioInvalido(){
        final String email = "tipo.invalido@test.com";


        Usuario usuarioInvalido = new Usuario();
        usuarioInvalido.setTipoUsuario('x');

        RegistroRequest request = new RegistroRequest();

        when(usuarioService.getUsuarioPorEmail(email)).thenReturn(usuarioInvalido);


        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            registroService.editarUsuario(email, request, null);
        });


        assertEquals("Não foi possível editar!", exception.getMessage());


        verify(usuarioService, times(1)).getUsuarioPorEmail(email);

        verify(clienteService, never()).getClientePorEmail(anyString());
        verify(prestadorService, never()).getPorEmail(anyString());
        verify(fisicaService, never()).editarPessoaFisica(any(), any());
        verifyNoInteractions(cloudinaryService);
    }
}