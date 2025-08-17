package com.workforyou.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity(name = "usuarios")
@Data
public class Usuario {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(unique = true)
    private String email;

    private String cpf;

    private String telefone;

    private String senha;

    public Usuario(){

    }

    public Usuario(Long id, String nome, String email, String cpf, String telefone, String senha) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.cpf = cpf;
        this.telefone = telefone;
        this.senha = senha;
    }
}