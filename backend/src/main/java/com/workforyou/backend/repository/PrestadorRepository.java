package com.workforyou.backend.repository;

import com.workforyou.backend.model.Prestador;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PrestadorRepository extends JpaRepository<Prestador, Long> {
    // Busca um Prestador navegando por:
    // Prestador -> pessoaFisica -> usuario -> email
    Optional<Prestador> findByPessoaFisicaUsuarioEmail(String email);
}