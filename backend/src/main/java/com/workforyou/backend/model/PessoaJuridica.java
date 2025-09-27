package com.workforyou.backend.model;

import jakarta.persistence.Column;

public class PessoaJuridica {

    private String nome;

    @Column(unique = true)
    private String cnpj;

    private String telefone;

}
