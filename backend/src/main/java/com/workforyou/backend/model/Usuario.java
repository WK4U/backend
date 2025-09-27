package com.workforyou.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity(name = "usuarios")
@Data // Gera getters, setters, toString, etc.
@NoArgsConstructor // Gera o construtor vazio para o JPA
@AllArgsConstructor // Gera o construtor com todos os campos
public class Usuario {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String documento;

    private String senha;

    private char TipoUsuario; // j ou f
}