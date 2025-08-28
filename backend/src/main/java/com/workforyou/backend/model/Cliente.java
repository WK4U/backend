package com.workforyou.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacionamento 1 para 1 com a PessoaFisica
    @OneToOne
    @JoinColumn(name = "id_pessoa_fisica", referencedColumnName = "id")
    private PessoaFisica pessoaFisica;


}