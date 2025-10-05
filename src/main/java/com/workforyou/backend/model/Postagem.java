package com.workforyou.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "postagens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Postagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Getter @Setter
    private String urlFoto;

    @Column(name = "descricao")
    @Getter @Setter
    private String descricaoPostagem;

    // Relacionamentos para a entidade a qual a foto pertence
    // Um dos campos abaixo será preenchido
    @ManyToOne
    @JoinColumn(name = "id_prestador")
    @Getter @Setter
    private Prestador prestador;

    @ManyToOne
    @JoinColumn(name = "id_servico")
    @Getter @Setter
    private Servico servico;

   
}
