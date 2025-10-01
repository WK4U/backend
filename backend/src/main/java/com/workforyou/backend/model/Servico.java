package com.workforyou.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "servicos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Getter @Setter
    private String nomeServico;

    @Column
    @Getter @Setter
    private String tipoServico;

    @Column
    @Getter @Setter
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "id_prestador")
    @Getter @Setter
    private Prestador prestador;
}