package com.workforyou.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "prestadores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prestador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String urlFoto;

    @Column(unique = true)
    private String email;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pessoa_juridica", referencedColumnName = "id")
    @JsonIgnoreProperties({"prestador"})
    private PessoaJuridica pessoaJuridica;

    @Column
    private String especialidade;

    // 🔥 ESSENCIAL PARA PARAR DUPLICAÇÃO E LOOP INFINITO
    @OneToMany(mappedBy = "prestador", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Servico> servicos;
}
