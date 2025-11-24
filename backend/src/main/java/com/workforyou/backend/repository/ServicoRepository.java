package com.workforyou.backend.repository;

import com.workforyou.backend.model.Prestador;
import com.workforyou.backend.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {
    Optional<Servico> findByTipoServicoAndPrestador( String tipoServico, Prestador prestador);
}
