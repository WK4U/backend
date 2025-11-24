package com.workforyou.backend.repository;

import com.workforyou.backend.model.Cliente;
import com.workforyou.backend.model.Prestador;
import com.workforyou.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    @Query("select c from clientes c where c.email = :email")
    Optional<Cliente> findClienteByEmail(@Param("email") String email);

    @Query("SELECT p FROM Prestador p WHERE p.email = :email")
    Optional<Prestador> findPrestadorByEmail(@Param("email") String email);

}

