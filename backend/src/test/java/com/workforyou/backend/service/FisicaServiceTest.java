package com.workforyou.backend.service;

import com.workforyou.backend.dto.RegistroRequest;
import com.workforyou.backend.model.PessoaFisica;
import com.workforyou.backend.repository.PessoaFisicaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FisicaServiceTest {

    @Mock
    private PessoaFisicaRepository pessoaFisicaRepository;

    @InjectMocks
    private FisicaService fisicaService;

    @Test
    public void testaCriarPessoaFisica(){
        RegistroRequest request = new RegistroRequest();
        request.setCpf("12345678900");
        request.setNome("Jorge");
        request.setTelefone("999999999");
        request.setDataNascimento(LocalDate.of(2000, 1, 1));

        PessoaFisica pessoaEsperada = new PessoaFisica();
        pessoaEsperada.setCpf("12345678900");
        pessoaEsperada.setNome("Jorge");
        pessoaEsperada.setTelefone("999999999");
        pessoaEsperada.setDataNascimento(LocalDate.of(2000, 1, 1));

        when(pessoaFisicaRepository.save(any(PessoaFisica.class))).thenReturn(pessoaEsperada);

        PessoaFisica resultado = fisicaService.criarPessoaFisica(request);

        assertNotNull(resultado);
        assertEquals("Jorge", resultado.getNome());
        assertEquals("12345678900", resultado.getCpf());
        assertEquals("999999999", resultado.getTelefone());
        assertEquals(LocalDate.of(2000, 1, 1), resultado.getDataNascimento());
        verify(pessoaFisicaRepository, times(1)).save(any(PessoaFisica.class));
    }

    @Test
    void testaCriarPessoaFisicaNegativo() {
        RegistroRequest request = new RegistroRequest();
        request.setCpf("");
        request.setNome("Jorge");
        request.setTelefone("999999999");
        request.setDataNascimento(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> fisicaService.criarPessoaFisica(request));

        assertEquals("Informe todos os dados necessários: CPF,NOME,TELEFONE E DATA NASCIMENTO", ex.getMessage());
        verify(pessoaFisicaRepository, never()).save(any());
    }

    @Test
    void testaVerificarCpf() {
        when(pessoaFisicaRepository.findByCpf("456")).thenReturn(Optional.empty());

        boolean resultado = fisicaService.verificarCpf("456");

        assertTrue(resultado);
        verify(pessoaFisicaRepository).findByCpf("456");
    }

    @Test
    void testaVerificarCpfFalse() {
        when(pessoaFisicaRepository.findByCpf("123")).thenReturn(Optional.of(new PessoaFisica()));

        boolean resultado = fisicaService.verificarCpf("123");

        assertFalse(resultado);
        verify(pessoaFisicaRepository).findByCpf("123");
    }

    @Test
    public void testaEditarPessoaFisica(){
        LocalDate dataAntiga = LocalDate.of(1980, 1, 1);
        LocalDate dataNova = LocalDate.of(1995, 5, 20);


        PessoaFisica pessoaAntiga = new PessoaFisica();
        pessoaAntiga.setNome("Nome Antigo");
        pessoaAntiga.setTelefone("11111111111");
        pessoaAntiga.setDataNascimento(dataAntiga);


        RegistroRequest request = new RegistroRequest();
        request.setNome("Nome Novo");
        request.setTelefone("99999999999");
        request.setDataNascimento(dataNova);


        when(pessoaFisicaRepository.save(any(PessoaFisica.class))).thenReturn(pessoaAntiga);


        PessoaFisica pessoaAtualizada = fisicaService.editarPessoaFisica(pessoaAntiga, request);


        assertEquals("Nome Novo", pessoaAtualizada.getNome());
        assertEquals("99999999999", pessoaAtualizada.getTelefone());
        assertEquals(dataNova, pessoaAtualizada.getDataNascimento());


        verify(pessoaFisicaRepository, times(1)).save(pessoaAntiga);
    }

    @Test
    public void testaEditarPessoaFisicaNegativo(){

        PessoaFisica pessoa = new PessoaFisica();
        pessoa.setNome("Teste Falha");

        RegistroRequest request = new RegistroRequest();
        request.setNome("Novo Nome");


        when(pessoaFisicaRepository.save(any(PessoaFisica.class)))
                .thenThrow(new IllegalStateException("Erro banco de dados"));


        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            fisicaService.editarPessoaFisica(pessoa, request);
        });


        assertEquals("Erro banco de dados", exception.getMessage());


        verify(pessoaFisicaRepository, times(1)).save(pessoa);
    }
}



