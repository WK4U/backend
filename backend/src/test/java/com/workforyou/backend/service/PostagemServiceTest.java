package com.workforyou.backend.service;

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

//    @Test
//    public void testaCriarNovaPostagem(){
//        String foto = "urlFoto";
//        String descricao = "baita postagem";
//        String cnpj = "12346789633456";
//        Long idServico = 1L;
//
//        Prestador prestador = new Prestador();
//        Servico servico = new Servico();
//        Postagem postagemSalva  = new Postagem();
//
//        when(postagemRepository.save(any(Postagem.class))).thenReturn(postagemSalva);
//        when(prestadorRepository.findByCnpj(cnpj)).thenReturn(Optional.of(prestador));
//        when(servicoRepository.findById(idServico)).thenReturn(Optional.of(servico));
//        when(postagemRepository.findByServicoId(idServico)).thenReturn(Optional.empty());
//
//        Postagem resultado = postagemService.salvarNovaPostagem(foto, descricao, cnpj, idServico,);
//
//        assertNotNull(resultado);
//        verify(postagemRepository).save(any(Postagem.class));
//    }

//    @Test
//    public void testaPrestadorNaoEncontradoNegativo(){
//        String foto = "foto";
//        String descricao = "baita postagem";
//        String cnpj = "99999999999999";
//        Long idServico = 1L;
//
//        when(postagemRepository.findByServicoId(idServico)).thenReturn(Optional.empty());
//        when(prestadorRepository.findByCnpj(cnpj)).thenReturn(Optional.empty());
//
//        RuntimeException ex = assertThrows(RuntimeException.class, ()-> {
//            postagemService.salvarNovaPostagem(foto, descricao, cnpj, idServico);
//        });
//
//        assertEquals("Prestador não encontrado para o CNPJ: " + cnpj, ex.getMessage());
//        verify(postagemRepository, never()).save(any(Postagem.class));
//    }
//
//    @Test public void testaServicoNaoEncontradoNegativo(){
//
//
//
//        String foto = "foto";
//        String nomeServico= "Limpeza";
//        String descricaoServico = "Postagem que ja existe";
//        String cnpj = "12345678000199";
//        Long idServico = 1L;
//        String tipoServico ="";
//        String descricaoPostagem ="";
//        Prestador prestador = new Prestador();
//
//        when(postagemRepository.findByServicoId(idServico)).thenReturn(Optional.empty());
//        when(prestadorRepository.findByCnpj(cnpj)).thenReturn(Optional.of(prestador));
//        when(servicoRepository.findById(idServico)).thenReturn(Optional.empty());
//
//        RuntimeException ex = assertThrows(RuntimeException.class, () ->
//                postagemService.salvarNovaPostagem(foto, nomeServico,
//                        tipoServico,
//                        descricaoServico,
//                        descricaoPostagem,
//                        cnpj,
//                        idServico)
//        );
//
//        assertEquals("Serviço não encontrado com o ID: " + idServico, ex.getMessage());
//        verify(postagemRepository, never()).save(any(Postagem.class));
//    }

    @Test
    public void testaPostagemJaCriada(){

        String foto = "foto";
        String nomeServico= "Limpeza";
        String descricaoServico = "Postagem que ja existe";
        String cnpj = "12345678000199";
        Long idServico = 1L;
        String tipoServico ="";
        String descricaoPostagem ="";

        Prestador prestador = new Prestador();
        Servico servico = new Servico();
        Postagem postagemSalva = new Postagem();

        when(postagemRepository.findByServicoId(idServico)).thenReturn(Optional.empty());
        when(prestadorRepository.findByCnpj(cnpj)).thenReturn(Optional.of(prestador));
        when(servicoRepository.findById(idServico)).thenReturn(Optional.of(servico));
        when(postagemRepository.save(any(Postagem.class))).thenReturn(postagemSalva);

//        Postagem resultado = postagemService.salvarNovaPostagem(foto, nomeServico,
//                tipoServico,
//                descricaoServico,
//                descricaoPostagem,
//                cnpj,
//                idServico);

//        assertNotNull(resultado);
        verify(postagemRepository).save(any(Postagem.class));
        verify(postagemRepository).findByServicoId(idServico);
        verify(prestadorRepository).findByCnpj(cnpj);
        verify(servicoRepository).findById(idServico);
    }

    @Test
    public void testaPostagemJaCriadaNegativo(){
        String foto = "foto";
        String nomeServico= "Limpeza";
        String descricaoServico = "Postagem que ja existe";
        String cnpj = "12345678000199";
        Long idServico = 1L;
        String tipoServico ="";
        String descricaoPostagem ="";

        when(postagemRepository.findByServicoId(idServico)).thenReturn(Optional.of(new Postagem()));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
//           postagemService.salvarNovaPostagem(foto, nomeServico,
//                    tipoServico,
//                    descricaoServico,
//                    descricaoPostagem,
//                    cnpj,
//                    idServico);
        });

        assertEquals("Postagem já criada com esse serviço!", ex.getMessage());
        verify(postagemRepository, never()).save(any(Postagem.class));
    }

    @Test
    public void testaEditarPostagem(){
        Postagem postagem = new Postagem();
        postagem.setId(1L);
        postagem.setDescricaoPostagem("Descrição antiga");
        postagem.setUrlFoto("foto_antiga.jpg");

        Long idServico = 1L;
        String novaFoto = "foto_nova.jpg";
        String novaDescricao = "Descrição nova";

        when(postagemRepository.findByServicoId(idServico)).thenReturn(Optional.of(postagem));
        when(postagemRepository.save(any(Postagem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Postagem resultado = postagemService.editarPostagem(idServico, novaFoto, novaDescricao);

        assertNotNull(resultado);
        assertEquals(novaFoto, resultado.getUrlFoto());
        assertEquals(novaDescricao, resultado.getDescricaoPostagem());

        verify(postagemRepository, times(2)).findByServicoId(idServico);
        verify(postagemRepository, times(1)).save(postagem);
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
    public void testaGetPostagem(){
        final String cnpj = "12345678000100";

        List<Postagem> postagensEsperadas = List.of(new Postagem(), new Postagem());


        when(postagemRepository.findByPrestadorPessoaJuridicaCnpj(cnpj))
                .thenReturn(Optional.of(postagensEsperadas));


        List<Postagem> postagensRetornadas = postagemService.getPostagemCnpj(cnpj);



        assertNotNull(postagensRetornadas);
        assertFalse(postagensRetornadas.isEmpty());
        assertEquals(2, postagensRetornadas.size());


        verify(postagemRepository, times(2)).findByPrestadorPessoaJuridicaCnpj(cnpj);
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

        final String tipoExistente = "Manutencao";


        List<Postagem> postagensEsperadas = List.of(new Postagem(), new Postagem(), new Postagem()); // 3 postagens

        when(postagemRepository.findByServicoTipoServico(tipoExistente))
                .thenReturn(Optional.of(postagensEsperadas));


        List<Postagem> postagensRetornadas = postagemService.getPostagemPorTipo(tipoExistente);


        assertNotNull(postagensRetornadas);
        assertFalse(postagensRetornadas.isEmpty());
        assertEquals(3, postagensRetornadas.size());


        verify(postagemRepository, times(2)).findByServicoTipoServico(tipoExistente);
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
        final Long idExistente = 1L;


        Postagem postagemEsperada = new Postagem();

        postagemEsperada.setDescricaoPostagem("Postagem Válida");


        when(postagemRepository.findById(idExistente))
                .thenReturn(Optional.of(postagemEsperada));


        Postagem postagemRetornada = postagemService.getPorId(idExistente);


        assertNotNull(postagemRetornada);
        assertEquals("Postagem Válida", postagemRetornada.getDescricaoPostagem());


        verify(postagemRepository, times(2)).findById(idExistente);
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
