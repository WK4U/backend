package com.workforyou.backend.service;

import com.workforyou.backend.dto.RegistroRequest;
import com.workforyou.backend.model.PessoaJuridica;
import com.workforyou.backend.model.Prestador;
import com.workforyou.backend.repository.PrestadorRepository;
import jakarta.persistence.Table;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PrestadorServiceTest {

    @Mock
    private PrestadorRepository prestadorRepository;

    @InjectMocks
    private PrestadorService prestadorService;

    @Test
    public void testaCriarPrestador(){
        RegistroRequest request = new RegistroRequest();
        request.setEmail("pessoa@gmail.com");
        request.setUriFoto("foto");
        request.setEspecialidade("reparos");

        PessoaJuridica pessoaJuridica = new PessoaJuridica();
        pessoaJuridica.setCnpj("12414423432599");

        Prestador prestadorSalvo = new Prestador();
        prestadorSalvo.setId(1L);
        prestadorSalvo.setEmail("pessoa@gmail.com");
        prestadorSalvo.setUrlFoto("foto");
        prestadorSalvo.setEspecialidade("reparos");
        prestadorSalvo.setPessoaJuridica(pessoaJuridica);

        when(prestadorRepository.save(any(Prestador.class))).thenReturn(prestadorSalvo);

        Prestador resultado = prestadorService.criarPrestador(request, pessoaJuridica);

        assertNotNull(resultado);
        assertEquals("pessoa@gmail.com", resultado.getEmail());
        assertEquals("foto", resultado.getUrlFoto());
        assertEquals("reparos", resultado.getEspecialidade());
        assertEquals("12414423432599", resultado.getPessoaJuridica().getCnpj());

        verify(prestadorRepository, times(1)).save(any(Prestador.class));
    }

    @Test
    public void testaCriarPrestadorNegativo(){
        RegistroRequest request = new RegistroRequest();
        request.setEmail("erro@email.com");
        request.setUriFoto("foto.png");
        request.setEspecialidade("Pintor");

        PessoaJuridica pessoaJuridica = new PessoaJuridica();
        pessoaJuridica.setCnpj("12414423432599");

        when(prestadorRepository.save(any(Prestador.class))).thenThrow(new RuntimeException("Erro ao salvar no banco"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            prestadorService.criarPrestador(request, pessoaJuridica);
        });

        assertEquals("Erro ao salvar no banco", ex.getMessage());
        verify(prestadorRepository, times(1)).save(any(Prestador.class));
    }

    @Test
    public void testaEditarPrestador(){
        final String fotoAntiga = "uri/foto/antiga.png";
        final String especialidadeAntiga = "Mecanica";

        final String fotoNova = "uri/foto/nova.png";
        final String especialidadeNova = "Eletricista";


        Prestador prestadorAntigo = new Prestador();
        prestadorAntigo.setUrlFoto(fotoAntiga);
        prestadorAntigo.setEspecialidade(especialidadeAntiga);

        RegistroRequest request = new RegistroRequest();
        request.setUriFoto(fotoNova);
        request.setEspecialidade(especialidadeNova);

        when(prestadorRepository.save(any(Prestador.class))).thenReturn(prestadorAntigo);


        Prestador prestadorAtualizado = prestadorService.editarPrestador(prestadorAntigo, request);


        assertEquals(fotoNova, prestadorAtualizado.getUrlFoto());
        assertEquals(especialidadeNova, prestadorAtualizado.getEspecialidade());


        verify(prestadorRepository, times(1)).save(prestadorAntigo);
    }

    @Test
    public void testaEditarUsuarioNegativo(){
        final String fotoOriginal = "uri/foto/original.png";
        final String especialidadeAntiga = "Mecanica";
        final String especialidadeNova = "Hidraulica";


        Prestador prestadorOriginal = new Prestador();
        prestadorOriginal.setUrlFoto(fotoOriginal);
        prestadorOriginal.setEspecialidade(especialidadeAntiga);


        RegistroRequest request = new RegistroRequest();
        request.setUriFoto(null);
        request.setEspecialidade(especialidadeNova);


        when(prestadorRepository.save(any(Prestador.class))).thenReturn(prestadorOriginal);


        Prestador prestadorAtualizado = prestadorService.editarPrestador(prestadorOriginal, request);


        assertEquals(especialidadeNova, prestadorAtualizado.getEspecialidade());

        assertEquals(fotoOriginal, prestadorAtualizado.getUrlFoto());


        verify(prestadorRepository, times(1)).save(prestadorOriginal);
    }

    @Test
    public void testaGetPorEmail(){

        final String emailExistente = "prestador.valido@exemplo.com";


        Prestador prestadorEsperado = new Prestador();
        prestadorEsperado.setEmail(emailExistente);
        prestadorEsperado.setUrlFoto("Foto");


        when(prestadorRepository.findByEmail(emailExistente))
                .thenReturn(Optional.of(prestadorEsperado));


        Prestador prestadorRetornado = prestadorService.getPorEmail(emailExistente);


        assertNotNull(prestadorRetornado);
        assertEquals(emailExistente, prestadorRetornado.getEmail());


        verify(prestadorRepository, times(2)).findByEmail(emailExistente);
    }

    @Test
    public void testaGetPorEmailNegativo(){

        final String emailExistente = "prestador.valido@exemplo.com";


        Prestador prestadorEsperado = new Prestador();
        prestadorEsperado.setEmail(emailExistente);
        prestadorEsperado.setUrlFoto("foto");


        when(prestadorRepository.findByEmail(emailExistente))
                .thenReturn(Optional.of(prestadorEsperado));


        Prestador prestadorRetornado = prestadorService.getPorEmail(emailExistente);


        assertNotNull(prestadorRetornado);
        assertEquals(emailExistente, prestadorRetornado.getEmail());


        verify(prestadorRepository, times(2)).findByEmail(emailExistente);
    }

}
