package com.workforyou.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fotos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Foto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String urlFoto;

    // Relacionamentos para a entidade a qual a foto pertence
    // Um dos campos abaixo será preenchido
    @ManyToOne
    @JoinColumn(name = "id_prestador")
    private Prestador prestador;

    @ManyToOne
    @JoinColumn(name = "id_servico")
    private Servico servico;

    @ManyToOne
    @JoinColumn(name = "id_contrato")
    private Contrato contrato;
}