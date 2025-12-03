package com.workforyou.backend.service;

import com.workforyou.backend.dto.RegistroRequest;
import com.workforyou.backend.model.Cliente;
import com.workforyou.backend.model.PessoaFisica;
import com.workforyou.backend.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    public void testaSalvarCliente() {
        PessoaFisica novaPessoaFisica = buildB(1L, "Jorge", "1234", "9991239", LocalDate.of(2005, 9, 16));

        RegistroRequest requestDeEntrada = new RegistroRequest();
        requestDeEntrada.setUriFoto("foto");
        requestDeEntrada.setEmail("jorge@gmail.com");

        Cliente clienteSalvoEsperado = new Cliente(1L, "jorge@gmail.com", "foto", novaPessoaFisica);

        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvoEsperado);
        Cliente clienteRetornado = clienteService.criarCliente(requestDeEntrada, novaPessoaFisica);

        assertNotNull(clienteRetornado);
        assertEquals(1L, clienteRetornado.getId());
        assertEquals("jorge@gmail.com", clienteRetornado.getEmail());
        assertEquals("foto", clienteRetornado.getUrlFoto());
        assertEquals(novaPessoaFisica, clienteRetornado.getPessoaFisica());

        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    public void testaSalvarClienteNegativo(){
        RegistroRequest request = new RegistroRequest();
        request.setEmail("matheusranheri@gmail.com");
        request.setUriFoto("foto");

        PessoaFisica pessoaFisica = new PessoaFisica();
        pessoaFisica.setCpf("12345678");

        when(clienteRepository.save(any(Cliente.class)))
                .thenThrow(new RuntimeException("Erro ao salvar cliente"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clienteService.criarCliente(request, pessoaFisica);
        });

        assertEquals("Erro ao salvar cliente", exception.getMessage());
    }

    @Test
    public void testaGetClientePorEmail(){

        final String email = "teste@gmail.com";

        Cliente cliente = new Cliente();
        cliente.setEmail(email);
        cliente.setUrlFoto("foto");

        when(clienteRepository.findByEmail(email)).thenReturn(Optional.of(cliente));

        Cliente clienteRetornado = clienteService.getClientePorEmail(email);

        assertNotNull(clienteRetornado);
        assertEquals(email, clienteRetornado.getEmail());

        verify(clienteRepository, times(2)).findByEmail(email);
    }

    @Test
    public void testaGetClienteEmailInvalido(){
        final String emailNaoExistente = "nao.existe@email.com";

        when(clienteRepository.findByEmail(emailNaoExistente))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clienteService.getClientePorEmail(emailNaoExistente);
        });

        assertEquals("Cliente com esse email não encontrado!", exception.getMessage());

        verify(clienteRepository, times(1)).findByEmail(emailNaoExistente);
    }

    @Test
    public void testaEditarCliente(){
        final String uriAntiga = "uri/foto/antiga.png";
        final String uriNova = "uri/foto/nova.png";

        Cliente clienteExistente = new Cliente();
        clienteExistente.setUrlFoto(uriAntiga);
        clienteExistente.setEmail("pessoa@gmail.com");

        RegistroRequest request = new RegistroRequest();
        request.setUriFoto(uriNova);

        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteExistente);


        Cliente clienteAtualizado = clienteService.editarCliente(clienteExistente, request);



        assertEquals(uriNova, clienteAtualizado.getUrlFoto());


        verify(clienteRepository, times(1)).save(clienteExistente);
    }

    @Test
    public void testaEditarClienteNegativo(){
        final String uriOriginal = "uri/foto/original.png";


        Cliente clienteExistente = new Cliente();
        clienteExistente.setUrlFoto(uriOriginal);
        clienteExistente.setEmail("pessoa@gamil.com");


        RegistroRequest request = new RegistroRequest();
        request.setUriFoto(null);


        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteExistente);


        Cliente clienteAtualizado = clienteService.editarCliente(clienteExistente, request);



        assertEquals(uriOriginal, clienteAtualizado.getUrlFoto());


        verify(clienteRepository, times(1)).save(clienteExistente);
    }

    private Cliente buildA(Long id, String email, String urlFoto, PessoaFisica pessoaFisica){
        return new Cliente(id, email, urlFoto, pessoaFisica);
    }

    private PessoaFisica buildB(Long id, String nome, String cpf, String telefone, LocalDate dataNascimento){
        return new PessoaFisica(id, nome, cpf, telefone, dataNascimento);
    }

}
