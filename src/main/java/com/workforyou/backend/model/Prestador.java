package com.workforyou.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "prestadores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prestador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Getter @Setter
    private String urlFoto;

    @Column(unique = true)
    @Getter @Setter
    private String email;

    // Relacionamento 1 para 1 com a PessoaFisica
    @OneToOne
    @JoinColumn(name = "id_pessoa_juridica", referencedColumnName = "id")
    @Getter @Setter
    private PessoaJuridica pessoaJuridica;

    // Campos específicos do prestador
    @Column
    @Getter @Setter
    private String especialidade;
}
