package com.workforyou.backend.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RegistroRequest {
    // Dados da PessoaFisica
    private String nome;
    private String cpf;
    private String telefone;
    private LocalDate dataNascimento;

    // Dados do Usuario
    private String email;
    private String senha;

    // Campo para definir o tipo de usuário
    private String tipoUsuario; // Ex: "CLIENTE" ou "PRESTADOR"

    //Dados do prestador
    private String especialidade;
    private String descricaoServico;
}