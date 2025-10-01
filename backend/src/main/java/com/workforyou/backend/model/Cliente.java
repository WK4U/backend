package com.workforyou.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @Getter @Setter
    private String email;

    @Getter @Setter
    @Column
    private String urlFoto;

    // Relacionamento 1 para 1 com a PessoaFisica
    @OneToOne
    @JoinColumn(name = "id_pessoa_fisica", referencedColumnName = "id")
    @Getter @Setter
    private PessoaFisica pessoaFisica; // j ou f
}