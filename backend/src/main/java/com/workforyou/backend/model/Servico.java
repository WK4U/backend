package com.workforyou.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "servicos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column
//    @Getter @Setter
//    private String nomeServico;

    @Column
    @Getter @Setter
    private String tipoServico;

//    @Column(name = "descricao_servico")
//    private String descricaoServico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_prestador")
    @JsonIgnoreProperties({"servicos", "pessoaJuridica"})
    private Prestador prestador;
}