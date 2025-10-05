package com.workforyou.backend.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO (Data Transfer Object) para receber os dados de registro
 * de uma nova postagem e serviço a partir do corpo da requisição JSON.
 */
@Data
public class PostagemRequest {

    // Dados do Serviço
    @NotBlank(message = "O nome do serviço é obrigatório")
    private String nomeServico;

    @NotBlank(message = "O tipo do serviço é obrigatório")
    private String tipoServico;

    @NotBlank(message = "A descrição do serviço é obrigatória")
    private String descricaoServico; // Mudei de 'descricaoS' para um nome claro

    // Dados da Postagem
    @NotBlank(message = "A descrição da postagem é obrigatória")
    private String descricaoPostagem; // Mudei de 'descricaoP' para um nome claro

    @NotBlank(message = "A foto é obrigatória")
    private String foto;

    // Chave de ligação
    @NotBlank(message = "O CNPJ é obrigatório")
    private String cnpj;
}
