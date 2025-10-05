package com.workforyou.backend.controller;

import com.workforyou.backend.dto.PostagemRequest;
import com.workforyou.backend.service.PostagemServicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid; // IMPORTANTE: Importar a anotação @Valid

@RestController
@RequestMapping("/postagem")
@RequiredArgsConstructor
public class PostagemController {

    @Autowired
    private PostagemServicoService postagemServicoService;

    @PostMapping(path = "/register" , consumes = {MediaType.APPLICATION_JSON_VALUE})
    // Adicionamos @Valid aqui para que as anotações do DTO sejam verificadas.
    public ResponseEntity<?> register(@Valid @RequestBody PostagemRequest request){
        try {
            // Se a validação falhar, o método register não será executado.
            // Se passar, a lógica de negócio é chamada:
            postagemServicoService.criarPostagemServico(
                    request.getNomeServico(),
                    request.getTipoServico(),
                    request.getDescricaoServico(),
                    request.getDescricaoPostagem(),
                    request.getCnpj(),
                    request.getFoto()
            );
        }catch (Exception e){
            return ResponseEntity.status(400).body(e.getMessage());
        }

        return ResponseEntity.status(201).body("Postagem criada!");
    }
}
