package com.workforyou.backend.service;

import com.workforyou.backend.dto.RegistroRequest;
import com.workforyou.backend.model.Cliente;
import com.workforyou.backend.model.PessoaFisica;
import com.workforyou.backend.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public Cliente criarCliente(RegistroRequest request, PessoaFisica pessoaFisica) {
        Cliente cliente = new Cliente();
        cliente.setUrlFoto(request.getUriFoto());
        cliente.setEmail(request.getEmail());
        cliente.setPessoaFisica(pessoaFisica);

        return clienteRepository.save(cliente);
    }

    public Cliente getClientePorEmail(String email){
        if(clienteRepository.findByEmail(email).isEmpty()){
            throw new RuntimeException("Cliente com esse email não encontrado!");
        }else{
            return clienteRepository.findByEmail(email).get();
        }
    }

    public Cliente editarCliente(Cliente cliente, RegistroRequest request) {
        if (request.getUriFoto() != null) {
            cliente.setUrlFoto(request.getUriFoto());
        }
        // Não editamos o email aqui, pois é usado para login.
        return clienteRepository.save(cliente);
    }

}