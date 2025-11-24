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
        String nomeServico = "Corte de Cabelo";
        String tipoServico = "Beleza";
        String descricaoServico = "Serviço profissional de corte";
        String cnpj = "12345678000199";

        PessoaJuridica pessoaJuridica = new PessoaJuridica();
        pessoaJuridica.setCnpj(cnpj);

        Prestador prestador = new Prestador();
        prestador.setPessoaJuridica(pessoaJuridica);

        Servico servicoSalvo = new Servico();
        servicoSalvo.setId(1L);
        servicoSalvo.setNomeServico(nomeServico);
        servicoSalvo.setTipoServico(tipoServico);
        servicoSalvo.setDescricaoServico(descricaoServico);
        servicoSalvo.setPrestador(prestador);

        when(prestadorRepository.findByCnpj(cnpj)).thenReturn(Optional.of(prestador));
        when(servicoRepository.save(any(Servico.class))).thenReturn(servicoSalvo);

        Servico resultado = servicoService.salvarServico(nomeServico, tipoServico, descricaoServico, cnpj);

        assertNotNull(resultado);
        assertEquals(nomeServico, resultado.getNomeServico());
        assertEquals(tipoServico, resultado.getTipoServico());
        assertEquals(descricaoServico, resultado.getDescricaoServico());
        assertEquals(prestador, resultado.getPrestador());
        assertEquals(cnpj, resultado.getPrestador().getPessoaJuridica().getCnpj());

        verify(prestadorRepository, times(1)).findByCnpj(cnpj);
    }

    @Test
    public void testaSalvarServicoNegativo(){
        String nomeServico = "Pintura Residencial";
        String tipoServico = "Reformas";
        String descricaoServico = "Serviço completo de pintura";
        String cnpj = "99999999000100";

        when(prestadorRepository.findByCnpj(cnpj)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
           servicoService.salvarServico(nomeServico, tipoServico, descricaoServico, cnpj);
        });

        assertEquals("Prestador não encontrado para o CNPJ: " + cnpj, ex.getMessage());

        verify(servicoRepository, never()).save(any(Servico.class));
        verify(prestadorRepository, times(1)).findByCnpj(cnpj);
    }

    @Test
    public void testaEditarServico(){
        final Long idExistente = 1L;

        Servico servicoExistente = new Servico();
        servicoExistente.setId(idExistente);
        servicoExistente.setNomeServico("Antigo");
        servicoExistente.setTipoServico("Velho");
        servicoExistente.setDescricaoServico("Descrição Original");

        final String novoNome = "Novo Nome";
        final String novoTipo = "Novo Tipo";
        final String novaDescricao = "Nova Descrição";

        when(servicoRepository.findById(idExistente))
                .thenReturn(Optional.of(servicoExistente));

        when(servicoRepository.save(any(Servico.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));


        Servico servicoAtualizado = servicoService.editarServico(
                idExistente, novoNome, novoTipo, novaDescricao
        );


        assertNotNull(servicoAtualizado);
        assertEquals(novoNome, servicoAtualizado.getNomeServico());
        assertEquals(novoTipo, servicoAtualizado.getTipoServico());
        assertEquals(novaDescricao, servicoAtualizado.getDescricaoServico());

        verify(servicoRepository, times(2)).findById(idExistente);
        verify(servicoRepository, times(1)).save(servicoAtualizado);
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
