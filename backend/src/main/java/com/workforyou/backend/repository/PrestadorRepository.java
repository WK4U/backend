package com.workforyou.backend.repository;

import com.workforyou.backend.model.Prestador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrestadorRepository extends JpaRepository<Prestador, Long> {

    @Query("SELECT p FROM prestadores p WHERE p.pessoaJuridica.cnpj = :cnpj")
    Optional<Prestador> findByCnpj(@Param("cnpj") String cnpj);

}
