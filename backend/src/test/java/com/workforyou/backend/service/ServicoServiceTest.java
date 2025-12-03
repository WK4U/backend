package com.workforyou.backend.service;

import com.workforyou.backend.model.PessoaJuridica;
import com.workforyou.backend.model.Prestador;
import com.workforyou.backend.model.Servico;
import com.workforyou.backend.repository.PessoaJuridicaRepository;
import com.workforyou.backend.repository.PrestadorRepository;
import com.workforyou.backend.repository.ServicoRepository;
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
public class ServicoServiceTest {

    @Mock
    private PrestadorRepository prestadorRepository;

    @Mock
    private ServicoRepository servicoRepository;

    @InjectMocks
    private ServicoService servicoService;

    @Test
    public void testaSalvarServico(){
        String cnpj = "12345678000111";
        String tipoServico = "Pintura";

        Prestador prestador = new Prestador();
        prestador.setId(1L);
        prestador.setPessoaJuridica(new PessoaJuridica());
        prestador.getPessoaJuridica().setCnpj(cnpj);

        Servico novoServico = new Servico();
        novoServico.setTipoServico(tipoServico);
        novoServico.setPrestador(prestador);

        when(prestadorRepository.findByCnpj(cnpj))
                .thenReturn(Optional.of(prestador));

        when(servicoRepository.findByTipoServicoAndPrestador(tipoServico, prestador))
                .thenReturn(Optional.empty());

        when(servicoRepository.save(any(Servico.class)))
                .thenReturn(novoServico);

        Servico servico = servicoService.salvarServico(tipoServico, cnpj);

        assertNotNull(servico);
        assertEquals(tipoServico, servico.getTipoServico());
        assertEquals(prestador, servico.getPrestador());

        verify(servicoRepository).save(any(Servico.class));
    }

    @Test
    public void testaSalvarServicoNegativoPrestadoraoEncontrado(){
        String cnpj = "00000000000000";
        String tipoServico = "Jardinagem";

        when(prestadorRepository.findByCnpj(cnpj))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> servicoService.salvarServico(tipoServico, cnpj)
        );

        assertEquals("Prestador não encontrado para o CNPJ: " + cnpj, ex.getMessage());

        verify(servicoRepository, never()).save(any());
    }

    @Test
    public void testaEditarServico(){
        Long id = 1L;
        String novoTipo = "Eletricista";

        Servico servico = new Servico();
        servico.setId(id);
        servico.setTipoServico("Pintura");

        when(servicoRepository.findById(id))
                .thenReturn(Optional.of(servico));

        when(servicoRepository.save(servico))
                .thenReturn(servico);

        Servico resultado = servicoService.editarServico(id, null, novoTipo, null);

        assertEquals(novoTipo, resultado.getTipoServico());
        verify(servicoRepository).save(servico);

    }

    @Test
    public void testaEditarUsuarioNegativo(){
        final Long idInexistente = 99L;

        when(servicoRepository.findById(idInexistente))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            servicoService.editarServico(idInexistente, "Nome", "Tipo", "Descricao");
        });

        assertEquals("Serviço não encontrado!", exception.getMessage());

        verify(servicoRepository, times(1)).findById(idInexistente);
        verify(servicoRepository, never()).save(any(Servico.class));
    }

    @Test
    public void testaExcluirServ(){
        final Long idServico = 15L;

        servicoService.excluirServ(idServico);

        verify(servicoRepository, times(1)).deleteById(idServico);
    }

    @Test
    public void testaExcluirServNegativo(){
        final Long idInexistente = 999L;

        servicoService.excluirServ(idInexistente);

        verify(servicoRepository, times(1)).deleteById(idInexistente);
    }
}
