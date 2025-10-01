package com.workforyou.backend.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;


@Data
public class RegistroRequest {

    public RegistroRequest() {
    }

    @Getter @Setter
    private String nome;

    @Getter @Setter
    private String telefone;

    // Dados da PessoaFisica
    @Getter @Setter
    private String cpf;

    @Getter @Setter
    private Calendar dataNascimento;

    // Dados da PessoaJuridica
    private String cnpj;

    // Dados do Usuario
    @Getter @Setter
    private String email;

    @Getter @Setter
    private String senha;

    // Campo para definir o tipo de usuário
    @Getter @Setter
    private String tipoUsuario; // Ex: "JURIDICO" ou "FISICO"

    //Dados do prestador
    @Getter @Setter
    private String especialidade;

    @Getter @Setter
    private String descricaoServico;

    @Getter @Setter
    private String uriFoto;


}