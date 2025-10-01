package com.workforyou.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "usuarios")
@Data // Gera getters, setters, toString, etc.
@NoArgsConstructor // Gera o construtor vazio para o JPA
@AllArgsConstructor // Gera o construtor com todos os campos
public class Usuario {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Getter @Setter
    private String email;

    @Column(unique = true)
    @Getter @Setter
    private String documento;

    @Column
    @Getter @Setter
    private String senha;

    @Column
    @Getter @Setter
    private char TipoUsuario; // j ou f


}