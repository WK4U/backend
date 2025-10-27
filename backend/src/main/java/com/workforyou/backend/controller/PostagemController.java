package com.workforyou.backend.controller;

import com.workforyou.backend.dto.PostagemRequest;
import com.workforyou.backend.model.Postagem;
import com.workforyou.backend.service.PostagemServicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid; // IMPORTANTE: Importar a anotação @Valid

import java.security.Principal;
import java.util.List;

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
            postagemServicoService.salvarPostagemServico(
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

    @GetMapping(path = "/get/{cnpj}",produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getPorCnpj(@PathVariable String cnpj){
        List<Postagem> postagens;

        try{

            postagens = postagemServicoService.getPostagensPorCnpj(cnpj);

        }catch (Exception e){
            return ResponseEntity.status(404).body(e.getMessage());
        }
        return ResponseEntity.status(200).body(postagens);
    }

    @GetMapping(path = "/get", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> get(){
        List<Postagem> postagens = postagemServicoService.getPostagens();

        return ResponseEntity.status(200).body(postagens);
    }

    @GetMapping(path = "/get/{tipoServico}",produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getPorTipoServico(@PathVariable String tipoServico){
        List<Postagem> postagens = postagemServicoService.getPostagensTipoServico(tipoServico);

        return ResponseEntity.status(200).body(postagens);
    }

    @DeleteMapping(path = "/delete")
    public ResponseEntity<?> deletarPostagemServico(@RequestParam("idServico") Long idServico, Principal principal, @RequestParam("idPostagem") Long idPostagem){

        if(principal == null){
            return ResponseEntity.status(401).body("Usuário não encontrado!");
        }

        try{

            String emailLogado = principal.getName();

            postagemServicoService.excluirPostagemServico(emailLogado,idServico,idPostagem);

        }catch (Exception e){
            return ResponseEntity.status(404).body("Erro de deleção: " + e.getMessage());
        }
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/edit/{idServico}",consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> editarPostagemServico(@PathVariable("idServico") Long idServico, @RequestBody PostagemRequest request){
        try{
            postagemServicoService.editarPostagemServico(idServico,
                    request.getNomeServico(),
                    request.getTipoServico(),
                    request.getDescricaoServico(),
                    request.getDescricaoPostagem(),
                    request.getFoto()
            );
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Erro de edição:" + e.getMessage());
        }
        return ResponseEntity.status(200).body("Serviço e postagem editados!");
    }
}
