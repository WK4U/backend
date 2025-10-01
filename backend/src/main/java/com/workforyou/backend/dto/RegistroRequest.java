package com.workforyou.backend.dto;

import lombok.Data;
import java.util.Calendar;

@Data
public class RegistroRequest {

    private String nome;
    private String telefone;

    private String uriFoto;

    // Dados da PessoaFisica
    private String cpf;
    private Calendar dataNascimento;

    // Dados da PessoaJuridica
    private String cnpj;

    // Dados do Usuario
    private String email;
    private String senha;

    // Campo para definir o tipo de usuário
    private String tipoUsuario; // Ex: "JURIDICO" ou "FISICO"

    //Dados do prestador
    private String especialidade;
    private String descricaoServico;
}