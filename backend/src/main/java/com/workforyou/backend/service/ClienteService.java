package com.workforyou.backend.service;

import com.workforyou.backend.model.Cliente;
import com.workforyou.backend.model.PessoaFisica;
import com.workforyou.backend.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public void salvarNovoCliente(String email, String foto, PessoaFisica pessoaFisica){
        Cliente cliente = new Cliente();
        cliente.setEmail(email);
        cliente.setUrlFoto(foto);
        cliente.setPessoaFisica(pessoaFisica);

        clienteRepository.save(cliente);
    }
}
