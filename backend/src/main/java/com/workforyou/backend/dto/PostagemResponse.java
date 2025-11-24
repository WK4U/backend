package com.workforyou.backend.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostagemResponse {
    private Long id;
    private String tipoServico;
    private String descricaoPostagem;
    private String foto;
    private UsuarioPerfilResponse prestador;
}