package com.workforyou.backend.service;

import com.workforyou.backend.dto.UsuarioPerfilResponse;
import com.workforyou.backend.model.*;
import com.workforyou.backend.repository.ClienteRepository;
import com.workforyou.backend.repository.PasswordResetCodeRepository;
import com.workforyou.backend.repository.PrestadorRepository;
import com.workforyou.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PrestadorRepository prestadorRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private RegistroService registroService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordResetCodeRepository passwordResetCodeRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    public void testaLoadUserByUserName(){

        String email = "teste@email.com";
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setSenha("123456");

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        UserDetails userDetails = usuarioService.loadUserByUsername(email);

        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals("123456", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());

        verify(usuarioRepository, times(1)).findByEmail(email);
    }

    @Test
    public void testaLoadByUserNameNegativo(){

        String email = "emailFake@gmail.com";

        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class, () -> {
            usuarioService.loadUserByUsername(email);
        });

        assertEquals("Usuário não encontrado: " + email, ex.getMessage());
        verify(usuarioRepository, times(1)).findByEmail(email);
    }

    @Test
    public void testaCriarUsuario(){
        String email = "teste@gmail.com";
        String senha = "123456";
        String senhaCriptografada = "$pass123";
        char tipoUsuario = 'C';
        String documento = "12345678900";

        Usuario usuarioSalvo = new Usuario();
        usuarioSalvo.setId(1L);
        usuarioSalvo.setEmail(email);
        usuarioSalvo.setSenha(senhaCriptografada);
        usuarioSalvo.setTipoUsuario(tipoUsuario);
        usuarioSalvo.setDocumento(documento);

        when(passwordEncoder.encode(senha)).thenReturn(senhaCriptografada);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSalvo);

        Usuario resultado = usuarioService.criarUsuario(email, senha, tipoUsuario, documento);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(email, resultado.getEmail());
        assertEquals(senhaCriptografada, resultado.getSenha());
        assertEquals(tipoUsuario, resultado.getTipoUsuario());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    public void testaCriarUsuarioNegativo(){

        String email = "teste@gmail.com";
        String senha = "123";
        char tipo = 'C';
        String documento = "111";

        when(passwordEncoder.encode(senha))
                .thenThrow(new RuntimeException("Erro ao criptografar senha"));

        assertThrows(RuntimeException.class, () -> {
            usuarioService.criarUsuario(email, senha, tipo, documento);
        });
    }

    @Test
    public void testaSolicitarRecuperacaoSenhaPorPin(){

        String email = "teste@email.com";
        Usuario usuario = new Usuario();
        usuario.setEmail(email);

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(passwordResetCodeRepository.save(any(PasswordResetCode.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        usuarioService.solicitarRecuperacaoSenhaPorPin(email);

        verify(usuarioRepository, times(1)).findByEmail(email);
        verify(passwordResetCodeRepository, times(1)).save(any(PasswordResetCode.class));
        verify(emailService, times(1)).send(eq(email), eq("Código de redefinição de senha"), contains("Seu código de redefinição de senha é:"));
    }

    @Test
    public void testaSolicitarRecuperacaoSenhaPorPinNegativo(){
        String email = "naoexiste@email.com";

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            usuarioService.solicitarRecuperacaoSenhaPorPin(email);
        });

        assertEquals("Usuário não encontardo", ex.getMessage());

        verify(usuarioRepository, times(1)).findByEmail(email);
        verify(passwordResetCodeRepository, never()).save(any());
        verify(emailService, never()).send(anyString(), anyString(), anyString());
    }

    @Test
    public void testaValidarPin(){

        String email = "teste@email.com";
        String code = "12345";

        PasswordResetCode resetCode = new PasswordResetCode();
        resetCode.setExpiracao(LocalDateTime.now().plusMinutes(5));
        resetCode.setUsado(false);

        when(passwordResetCodeRepository.findByUsuarioEmailAndCode(email, code))
                .thenReturn(Optional.of(resetCode));

        boolean resultado = usuarioService.validarPin(email, code);

        assertTrue(resultado);
        verify(passwordResetCodeRepository, times(1))
                .findByUsuarioEmailAndCode(email, code);
    }

    @Test
    public void testaValidarPinNegativo(){
        String email = "teste@email.com";
        String code = "99999";

        when(passwordResetCodeRepository.findByUsuarioEmailAndCode(email, code))
                .thenReturn(Optional.empty());

        boolean resultado = usuarioService.validarPin(email, code);

        assertFalse(resultado);
        verify(passwordResetCodeRepository, times(1))
                .findByUsuarioEmailAndCode(email, code);
    }

    @Test
    public void testaAtualizarSenhaComToken(){
        String code = "abc123";
        String novaSenha = "senhaNova123";
        String senhaCriptografada = "senhaCriptoXYZ";

        Usuario usuario = new Usuario();
        usuario.setSenha("senhaAntiga");

        PasswordResetCode resetCode = new PasswordResetCode();
        resetCode.setUsado(false);
        resetCode.setExpiracao(LocalDateTime.now().plusMinutes(10));
        resetCode.setUsuario(usuario);

        when(passwordResetCodeRepository.findByCode(code))
                .thenReturn(Optional.of(resetCode));

        when(passwordEncoder.encode(novaSenha))
                .thenReturn(senhaCriptografada);

        boolean resultado = usuarioService.atualizarSenhaComToken(code, novaSenha);

        assertTrue(resultado, "O método deveria retornar true");

        assertEquals(
                senhaCriptografada,
                usuario.getSenha(),
                "A senha do usuário deveria ter sido atualizada."
        );

        assertTrue(
                resetCode.isUsado(),
                "O código deveria ter sido marcado como usado."
        );

        verify(usuarioRepository, times(1)).save(usuario);
        verify(passwordResetCodeRepository, times(1)).save(resetCode);
    }

    @Test
    public void testaAtualizarSenhaComTokenNegativo(){
        String codigoInvalido = "codigoInvalido";
        String novaSenha = "novaSenha123";

        when(passwordResetCodeRepository.findByCode(codigoInvalido))
                .thenReturn(Optional.empty());

        boolean resultado = usuarioService.atualizarSenhaComToken(codigoInvalido, novaSenha);
        assertFalse(resultado);

        verify(usuarioRepository, never()).save(any());
        verify(passwordResetCodeRepository, never()).save(any());
    }

    @Test
    public void testaVerificarEmailExstente(){
        String email = "teste@exemplo.com";
        Usuario usuario = new Usuario();
        usuario.setEmail(email);

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));


        boolean resultado = usuarioService.verificarEmailExistente(email);


        assertTrue(resultado);
        verify(usuarioRepository, times(1)).findByEmail(email);

    }

    @Test
    public void testaVerificarEmailExistenteNegativo(){
        String email = "naoexiste@exemplo.com";
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.empty());

        boolean resultado = usuarioService.verificarEmailExistente(email);


        assertFalse(resultado);
        verify(usuarioRepository, times(1)).findByEmail(email);
    }

    @Test
    public void testaGetUsuarioPorEmail(){
        final String emailExistente = "usuario.valido@exemplo.com";

        Usuario usuarioEsperado = new Usuario();
        usuarioEsperado.setEmail(emailExistente);
        usuarioEsperado.setDocumento("21343254635");

        when(usuarioRepository.findByEmail(emailExistente))
                .thenReturn(Optional.of(usuarioEsperado));

        Usuario usuarioRetornado = usuarioService.getUsuarioPorEmail(emailExistente);


        assertNotNull(usuarioRetornado);
        assertEquals(emailExistente, usuarioRetornado.getEmail());

        verify(usuarioRepository, times(2)).findByEmail(emailExistente);
    }

    @Test
    public void testaGetUsuarioPorEmailNegativo(){
        final String emailInexistente = "nao.existe@exemplo.com";

        when(usuarioRepository.findByEmail(emailInexistente))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.getUsuarioPorEmail(emailInexistente);
        });

        assertEquals("Usuário com esse email não encontrado!", exception.getMessage());

        verify(usuarioRepository, times(1)).findByEmail(emailInexistente);
    }

    @Test
    public void testaObterPerfil(){
        String email = "cliente@email.com";

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail(email);
        usuario.setTipoUsuario('F');
        usuario.setDocumento("12345678900");

        PessoaFisica pf = new PessoaFisica();
        pf.setNome("João Silva");
        pf.setCpf("12345678900");
        pf.setTelefone("99999-9999");
        pf.setDataNascimento(LocalDate.of(2000, 1, 1));

        Cliente cliente = new Cliente();
        cliente.setEmail(email);
        cliente.setPessoaFisica(pf);
        cliente.setUrlFoto("foto_cliente.png");

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(clienteRepository.findByEmail(email)).thenReturn(Optional.of(cliente));
        when(prestadorRepository.findByEmail(email)).thenReturn(Optional.empty());

        UsuarioPerfilResponse response = usuarioService.obterPerfil(email);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("João Silva", response.getNome());
        assertEquals(email, response.getEmail());
        assertEquals("12345678900", response.getCpf());
        assertNull(response.getCnpj());
        assertEquals("99999-9999", response.getTelefone());
        assertEquals("foto_cliente.png", response.getFoto());
        assertEquals(LocalDate.of(2000, 1, 1), response.getDataNascimento());
    }

    @Test
    public void testaObterPerfilNegativo(){
        String email = "inexistente@email.com";

        when(usuarioRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> usuarioService.obterPerfil(email)
        );

        assertEquals("Usuário não encontrado", ex.getMessage());
    }
}
