package com.workforyou.backend.service;

import com.workforyou.backend.dto.RegistroRequest;
import com.workforyou.backend.model.PessoaFisica;
import com.workforyou.backend.model.PessoaJuridica;
import com.workforyou.backend.repository.PessoaJuridicaRepository;
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
public class JuridicaServiceTest {

    @Mock
    private PessoaJuridicaRepository pessoaJuridicaRepository;

    @InjectMocks
    private JuridicaService juridicaService;

    @Test
    public void testaCriarPessoaJuridicaService(){
        RegistroRequest request = new RegistroRequest();
        request.setNome("matheus");
        request.setCnpj("12345676543245");
        request.setTelefone("24587857400");

        PessoaJuridica pessoaEsperada = new PessoaJuridica();
        pessoaEsperada.setNome("matheus");
        pessoaEsperada.setCnpj("12345676543245");
        pessoaEsperada.setTelefone("24587857400");

        when(pessoaJuridicaRepository.save(any(PessoaJuridica.class))).thenReturn(pessoaEsperada);

        PessoaJuridica resultado = juridicaService.criarPessoaJuridica(request);

        assertNotNull(resultado);
        assertEquals("matheus", resultado.getNome());
        assertEquals("12345676543245", resultado.getCnpj());
        assertEquals("24587857400", resultado.getTelefone());
        verify(pessoaJuridicaRepository, times(1)).save(any(PessoaJuridica.class));
    }

    @Test
    public void testaCriarPessoaJuridicaNegativo(){
        RegistroRequest request = new RegistroRequest();
        request.setNome("matheus");
        request.setCnpj("12345676543245");
        request.setTelefone("24587857400");

        when(pessoaJuridicaRepository.save(any(PessoaJuridica.class)))
                .thenThrow(new RuntimeException("Erro ao salvar no banco"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> juridicaService.criarPessoaJuridica(request));

        assertEquals("Erro ao salvar no banco", ex.getMessage());
        verify(pessoaJuridicaRepository, times(1)).save(any(PessoaJuridica.class));
    }

    @Test
    public void testaVerificarCnpj(){
        when(pessoaJuridicaRepository.findByCnpj("45632443525587")).thenReturn(Optional.empty());

        boolean resultado =  juridicaService.verificarCnpj("45632443525587");

        assertTrue(resultado);
        verify(pessoaJuridicaRepository).findByCnpj("45632443525587");
    }

    @Test
    public void testaVerificarCnpjNegativo(){
        when(pessoaJuridicaRepository.findByCnpj("54386546729492")).thenReturn(Optional.of(new PessoaJuridica()));

        boolean resultado =  juridicaService.verificarCnpj("54386546729492");

        assertFalse(resultado);
        verify(pessoaJuridicaRepository).findByCnpj("54386546729492");
    }

    @Test
    public void testaEditarPessoaJuridica(){
        PessoaJuridica pessoaAntiga = new PessoaJuridica();
        pessoaAntiga.setNome("Nome Antigo");
        pessoaAntiga.setTelefone("11111111111");
        final String cnpjOriginal = "11423535";
        pessoaAntiga.setCnpj(cnpjOriginal);


        RegistroRequest request = new RegistroRequest();
        request.setNome("Nome Novo");
        request.setTelefone("99999999999");
        request.setCnpj("35235645757");


        when(pessoaJuridicaRepository.save(any(PessoaJuridica.class))).thenReturn(pessoaAntiga);


        PessoaJuridica pessoaAtualizada = juridicaService.editarPessoaJuridica(pessoaAntiga, request);


        assertEquals("Nome Novo", pessoaAtualizada.getNome());
        assertEquals("99999999999", pessoaAtualizada.getTelefone());

        assertEquals(cnpjOriginal, pessoaAtualizada.getCnpj());


        verify(pessoaJuridicaRepository, times(1)).save(pessoaAntiga);
    }

    @Test
    public void testaEditarPessoaJuridicaNegativo(){
        final String cnpjOriginal = "11423535";
        final String cnpjInvalidoNoRequest = "99999999999";


        PessoaJuridica pessoaAntiga = new PessoaJuridica();
        pessoaAntiga.setNome("Nome Antigo");
        pessoaAntiga.setTelefone("11111111111");
        pessoaAntiga.setCnpj(cnpjOriginal);


        RegistroRequest request = new RegistroRequest();
        request.setNome("Nome Novo");
        request.setTelefone("99999999999");
        request.setCnpj(cnpjInvalidoNoRequest);


        when(pessoaJuridicaRepository.save(any(PessoaJuridica.class))).thenReturn(pessoaAntiga);


        PessoaJuridica pessoaAtualizada = juridicaService.editarPessoaJuridica(pessoaAntiga, request);



        assertEquals(cnpjOriginal, pessoaAtualizada.getCnpj());


        assertEquals("Nome Novo", pessoaAtualizada.getNome());


        verify(pessoaJuridicaRepository, times(1)).save(pessoaAntiga);
    }
}


