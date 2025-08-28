package com.workforyou.backend.repository;

import com.workforyou.backend.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // Busca um Cliente navegando por:
    // Cliente -> pessoaFisica -> usuario -> email
    Optional<Cliente> findByPessoaFisicaUsuarioEmail(String email);
}