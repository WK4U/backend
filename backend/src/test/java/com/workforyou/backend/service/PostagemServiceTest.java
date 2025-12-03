package com.workforyou.backend.service;

import com.workforyou.backend.model.PessoaJuridica;
import com.workforyou.backend.model.Postagem;
import com.workforyou.backend.model.Prestador;
import com.workforyou.backend.model.Servico;
import com.workforyou.backend.repository.PostagemRepository;
import com.workforyou.backend.repository.PrestadorRepository;
import com.workforyou.backend.repository.ServicoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostagemServiceTest {

    @Mock
    private PostagemRepository postagemRepository;

    @Mock
    private PrestadorRepository prestadorRepository;

    @Mock
    private ServicoRepository servicoRepository;

    @InjectMocks
    private PostagemService postagemService;

    @Test
    public void testaCriarNovaPostagem(){
        Long idServico = 1L;
        String cnpj = "12345678000199";

        Servico servico = new Servico();
        servico.setId(idServico);

        when(servicoRepository.findById(idServico))
                .thenReturn(Optional.of(servico));

        PessoaJuridica pj = new PessoaJuridica();
        pj.setCnpj(cnpj);

        Prestador prestador = new Prestador();
        prestador.setPessoaJuridica(pj);

        when(prestadorRepository.findByCnpj(cnpj))
                .thenReturn(Optional.of(prestador));

        Postagem postagemSalva = new Postagem();
        postagemSalva.setId(10L);

        when(postagemRepository.save(any(Postagem.class)))
                .thenReturn(postagemSalva);

        Postagem resultado = postagemService.salvarNovaPostagem(
                "foto.jpg",
                idServico,
                "LIMPEZA",
                5L,
                "desc",
                cnpj
        );

        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        verify(postagemRepository).save(any(Postagem.class));
    }

    @Test
    public void testaPrestadorNaoEncontradoNegativo(){
        Long idServico = 1L;
        String cnpj = "12345678000199";

        Servico servico = new Servico();
        servico.setId(idServico);

        when(servicoRepository.findById(idServico))
                .thenReturn(Optional.of(servico));

        when(prestadorRepository.findByCnpj(cnpj))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                postagemService.salvarNovaPostagem(
                        "foto.jpg",
                        idServico,
                        "LIMPEZA",
                        5L,
                        "desc",
                        cnpj
                )
        );

        assertEquals("Prestador não encontrado para o CNPJ: " + cnpj, ex.getMessage());
        verify(postagemRepository, never()).save(any());
    }

    @Test public void testaServicoNaoEncontradoNegativo(){
        Long idServico = 1L;
        String cnpj = "12345678000199";

        when(servicoRepository.findById(idServico))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
                postagemService.salvarNovaPostagem(
                "foto.jpg",
                idServico,
            "LIMPEZA",
            5L,
            "desc",
                cnpj
            )
        );

    verify(prestadorRepository, never()).findByCnpj(any());
    verify(postagemRepository, never()).save(any());
    }

    @Test
    public void testaEditarPostagem(){
        Long idServico = 1L;

        Postagem postagemExistente = new Postagem();
        postagemExistente.setId(10L);
        postagemExistente.setUrlFoto("foto_antiga.jpg");
        postagemExistente.setDescricaoPostagem("Descricao antiga");


        when(postagemRepository.findByServicoId(idServico))
                .thenReturn(Optional.of(postagemExistente));

        when(postagemRepository.save(any(Postagem.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Postagem resultado = postagemService.editarPostagem(
                idServico,
                "foto_nova.jpg",
                "Descricao nova"
        );

        assertEquals("foto_nova.jpg", resultado.getUrlFoto());
        assertEquals("Descricao nova", resultado.getDescricaoPostagem());

        verify(postagemRepository, times(1)).save(postagemExistente);
    }

    @Test
    void testaEditarPostagemNegativo() {
        Long idServico = 99L;


        when(postagemRepository.findByServicoId(idServico)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postagemService.editarPostagem(idServico, "foto.jpg", "Descrição");
        });


        assertEquals("Postagem não encontrada!", exception.getMessage());


        verify(postagemRepository, times(1)).findByServicoId(idServico);


        verify(postagemRepository, never()).save(any());
    }

    @Test
    public void testaAtualizarCamposPostagem(){
        Long idPostagem = 1L;

        Postagem postagem = new Postagem();
        postagem.setId(idPostagem);
        postagem.setTipoServico("ANTIGO");
        postagem.setDescricaoPostagem("Desc antiga");
        postagem.setUrlFoto("url_antiga.jpg");

        when(postagemRepository.findById(idPostagem)).thenReturn(Optional.of(postagem));

        when(postagemRepository.save(any(Postagem.class))).thenAnswer(inv -> inv.getArgument(0));

        Postagem resultado = postagemService.atualizarCamposPostagem(
                idPostagem,
                "NOVO TIPO",
                "Nova descrição",
                "nova_foto.png"
        );

        assertNotNull(resultado);
        assertEquals("NOVO TIPO", resultado.getTipoServico());
        assertEquals("Nova descrição", resultado.getDescricaoPostagem());
        assertEquals("nova_foto.png", resultado.getUrlFoto());

        verify(postagemRepository).findById(idPostagem);
        verify(postagemRepository).save(postagem);
    }

    @Test
    public void testaPostagemNaoEncontrada(){
        Long idPostagem = 1L;

        when(postagemRepository.findById(idPostagem))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> postagemService.atualizarCamposPostagem(
                        idPostagem,
                        "Tipo novo",
                        "Desc nova",
                        "foto.png"
                )
        );

        assertEquals("Postagem não encontrada!", ex.getMessage());

        verify(postagemRepository).findById(idPostagem);
        verify(postagemRepository, never()).save(any());
    }

    @Test
    public void testaGetPostagemCnpj(){
        String cnpj = "12345678000199";

        Postagem p1 = new Postagem();
        Postagem p2 = new Postagem();

        List<Postagem> lista = List.of(p1, p2);

        when(postagemRepository.findByPrestadorPessoaJuridicaCnpj(cnpj))
                .thenReturn(Optional.of(lista));

        List<Postagem> resultado = postagemService.getPostagemCnpj(cnpj);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertSame(lista, resultado);

        verify(postagemRepository).findByPrestadorPessoaJuridicaCnpj(cnpj);
    }

    @Test
    public void testaGetPostagemCnpjNegativo(){
        String cnpj = "12345678000199";

        when(postagemRepository.findByPrestadorPessoaJuridicaCnpj(cnpj))
                .thenReturn(Optional.empty());


        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> postagemService.getPostagemCnpj(cnpj)
        );

        assertEquals("Não há postagens!", ex.getMessage());

        verify(postagemRepository).findByPrestadorPessoaJuridicaCnpj(cnpj);
    }

    @Test
    public void testaGetPostagem(){
        Postagem p1 = new Postagem();
        Postagem p2 = new Postagem();

        List<Postagem> lista = List.of(p1, p2);

        when(postagemRepository.findAll()).thenReturn(lista);


        List<Postagem> resultado = postagemService.getPostagem();


        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertSame(lista, resultado);

        verify(postagemRepository).findAll();
    }

    @Test
    public void testaGetPostagemNegativo(){
        final String cnpj = "12345678000100";


        when(postagemRepository.findByPrestadorPessoaJuridicaCnpj(cnpj))
                .thenReturn(Optional.empty());



        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postagemService.getPostagemCnpj(cnpj);
        });


        assertEquals("Não há postagens!", exception.getMessage());


        verify(postagemRepository, times(1)).findByPrestadorPessoaJuridicaCnpj(cnpj);
    }

    @Test
    public void testaGetPostagemPorTipo(){
        String tipo = "Hidráulica";

        Postagem p1 = new Postagem();
        Postagem p2 = new Postagem();

        List<Postagem> lista = List.of(p1, p2);

        when(postagemRepository.findByServicoTipoServico(tipo))
                .thenReturn(Optional.of(lista));

        List<Postagem> resultado = postagemService.getPostagemPorTipo(tipo);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertSame(lista, resultado);

        verify(postagemRepository).findByServicoTipoServico(tipo);
    }

    @Test
    public void testaGetPostagemPorTipoNegativo(){
        final String tipoInexistente = "Tipo Inexistente";


        when(postagemRepository.findByServicoTipoServico(tipoInexistente))
                .thenReturn(Optional.empty());


        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postagemService.getPostagemPorTipo(tipoInexistente);
        });


        assertEquals("Não há postagens para esse tipo de serviço!", exception.getMessage());


        verify(postagemRepository, times(1)).findByServicoTipoServico(tipoInexistente);
    }

    @Test
    public void testaExcluirPost(){
        final Long idPostagem = 5L;

        postagemService.excluirPost(idPostagem);


        verify(postagemRepository, times(1)).deleteById(idPostagem);
    }

    @Test
    public void testaExcluirPostNegativo(){
        final Long idInexistente = 999L;


        postagemService.excluirPost(idInexistente);


        verify(postagemRepository, times(1)).deleteById(idInexistente);
    }

    @Test
    public void testaGetPorId(){
        Long id = 10L;

        Postagem postagem = new Postagem();
        postagem.setId(id);

        when(postagemRepository.findById(id))
                .thenReturn(Optional.of(postagem));

        Postagem resultado = postagemService.getPorId(id);

        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertSame(postagem, resultado);

        verify(postagemRepository).findById(id);
    }

    @Test
    public void testaGetPorIdNegativo(){
        final Long idInexistente = 99L;


        when(postagemRepository.findById(idInexistente))
                .thenReturn(Optional.empty());


        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postagemService.getPorId(idInexistente);
        });


        assertEquals("Postagem não encontrada!", exception.getMessage());


        verify(postagemRepository, times(1)).findById(idInexistente);
    }

}
