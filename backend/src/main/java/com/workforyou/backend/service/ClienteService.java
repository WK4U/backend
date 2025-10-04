package com.workforyou.backend.service;

import com.workforyou.backend.dto.RegistroRequest;
import com.workforyou.backend.model.Cliente;
import com.workforyou.backend.model.PessoaFisica;
import com.workforyou.backend.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public Cliente criarCliente(RegistroRequest request, PessoaFisica pessoaFisica) {
        Cliente cliente = new Cliente();
        cliente.setUrlFoto(request.getUriFoto());
        cliente.setEmail(request.getEmail());
        cliente.setPessoaFisica(pessoaFisica);

        return clienteRepository.save(cliente);
    }
}