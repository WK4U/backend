package com.workforyou.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pessoas_juridicas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PessoaJuridica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter @Setter
    @Column
    private String nome;

    @Getter @Setter
    @Column(unique = true)
    private String cnpj;

    @Getter @Setter
    @Column
    private String telefone;
}

