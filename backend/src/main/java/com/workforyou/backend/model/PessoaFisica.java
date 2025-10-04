package com.workforyou.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Table(name = "pessoas_fisicas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PessoaFisica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter @Setter
    @Column
    private String nome;

    @Getter @Setter
    @Column(unique = true)
    private String cpf;

    @Getter @Setter
    @Column
    private String telefone;

    @Getter @Setter
    @Column
    private LocalDate dataNascimento;


}
