package com.workforyou.backend.repository;

import com.workforyou.backend.model.Postagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostagemRepository extends JpaRepository<Postagem,Long> {
}
