package com.workforyou.backend.repository;

import com.workforyou.backend.model.PessoaFisica;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PessoaFisicaRepository extends JpaRepository<PessoaFisica, Long> {
    // Busca uma PessoaFisica pelo campo "cpf".
    Optional<Object> findByCpf(String email);
}