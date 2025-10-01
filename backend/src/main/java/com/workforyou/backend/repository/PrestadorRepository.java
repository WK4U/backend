package com.workforyou.backend.repository;

import com.workforyou.backend.model.Prestador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrestadorRepository extends JpaRepository<Prestador, Long> {
    // Busca um Prestador navegando por:
    // Prestador -> pessoaJuridica -> usuario -> CNPJ
    Optional<Prestador> findByPessoaJuridicaCnpj(String cnpj);
}
