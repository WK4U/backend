package com.workforyou.backend.repository;

import com.workforyou.backend.model.PessoaFisica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PessoaFisicaRepository extends JpaRepository<PessoaFisica, Long> {
    // Busca uma PessoaFisica pelo campo "cpf".
    Optional<Object> findByCpf(String cpf);
}
