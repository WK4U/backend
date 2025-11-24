package com.workforyou.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

@Entity
@Table(name = "postagens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Postagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // URL da foto armazenada (ex: S3 / local)
    @Column(name = "url_foto")
    private String urlFoto;

    // Descrição da postagem (texto principal). Ajuste o nome da coluna conforme sua migração.
    @Column(name = "descricao_postagem")  // se você criou a coluna nova
    // @Column(name = "descricao")        // use esta linha se manteve a coluna antiga
    private String descricaoPostagem;

    // Campos adicionais do DTO
//    @Column(name = "nome_servico")
//    private String nomeServico;

    @Column(name = "tipo_servico")
    private String tipoServico;

//    @Column(name = "descricao_servico")
//    private String descricaoServico;

    @Column(name = "cnpj")
    private String cnpj;

    // Relacionamentos existentes
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"servicos", "postagens", "pessoaJuridica"})
    @JoinColumn(name = "id_prestador")
    private Prestador prestador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"prestador"})
    @JoinColumn(name = "id_servico")
    private Servico servico;
}