//package com.workforyou.backend.service;
//
//import com.workforyou.backend.model.Usuario;
//import com.workforyou.backend.repository.UsuarioRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.equalTo;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//
//public class TestUsuarioService {
//
//    @Mock
//    private UsuarioRepository usuarioRepository;
//
//    @InjectMocks
//    private UsuarioService usuarioService;
//
//    @BeforeEach
//    public void setUp(){
//        MockitoAnnotations.initMocks(this);
//    }
//
//    @Test
//    void testaSalvarUsuario(){
//
//        UsuarioRepository usuarioRepository1 = mock(UsuarioRepository.class);
//        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
//
//        UsuarioService usuarioService1 = new UsuarioService(usuarioRepository1, passwordEncoder);
//
//        Usuario usuarioNovo = build(1L, "Matheus", "matheusranheri@gmail.com", "1234");
//
//        when(passwordEncoder.encode("1234")).thenReturn("senhaCriptografada");
//        when(usuarioRepository1.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        Usuario retorno = usuarioService.salvarUsuario(usuarioNovo);
//
//        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
//        verify(usuarioRepository, times(1)).save(captor.capture());
//
//        Usuario capturado = captor.getValue();
//
//        assertThat(capturado.getId(), equalTo(1L));
//        assertThat(capturado.getNome(), equalTo("Matheus"));
//        assertThat(capturado.getEmail(), equalTo("matheusranheri@gmail.com"));
//        assertThat(capturado.getSenha(), equalTo("senhaCriptografada"));
//        assertThat(retorno.getSenha(), equalTo("senhaCriptografada"));
//    }
//
//    private Usuario build(Long id, String nome, String email, String senha){
//        return new Usuario(id, nome, email, senha);
//    }
//
//}
