package com.workforyou.backend.model;

import com.workforyou.backend.model.PessoaFisica;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "prestadores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prestador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacionamento 1 para 1 com a PessoaFisica
    @OneToOne
    @JoinColumn(name = "id_pessoa_fisica", referencedColumnName = "id")
    private PessoaFisica pessoaFisica;

    // Campos específicos do prestador
    private String especialidade;
    private String descricaoServico;
}