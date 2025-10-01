package com.workforyou.backend.repository;

import com.workforyou.backend.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicoRepository extends JpaRepository<Servico,Long> {
}
