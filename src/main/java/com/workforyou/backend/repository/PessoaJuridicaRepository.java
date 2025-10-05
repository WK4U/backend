package com.workforyou.backend.repository;

import com.workforyou.backend.model.PessoaJuridica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PessoaJuridicaRepository extends JpaRepository<PessoaJuridica, Long> {
    // Busca uma PessoaJurica pelo campo "cnpj".
    Optional<Object> findByCnpj(String cnpj);
}
