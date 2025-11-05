package com.workforyou.backend.controller;

import com.workforyou.backend.dto.PostagemRequest;
import com.workforyou.backend.model.Postagem;
import com.workforyou.backend.model.Usuario;
import com.workforyou.backend.service.PostagemServicoService;
import com.workforyou.backend.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/postagem")
@RequiredArgsConstructor
public class PostagemController {

    @Autowired
    private PostagemServicoService postagemServicoService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping(path = "/register" , consumes = {MediaType.APPLICATION_JSON_VALUE})
    // Adicionamos @Valid aqui para que as anotações do DTO sejam verificadas.
    public ResponseEntity<?> register(@Valid @RequestBody PostagemRequest request, Principal principal){

        if (principal == null) {
            return ResponseEntity.status(401).body("Usuário não autenticado.");
        }

        try {

            String emailLogado = principal.getName();
            Usuario usuarioLogado = usuarioService.getUsuarioPorEmail(emailLogado);
            String cnpjVerificado = usuarioLogado.getDocumento();

            postagemServicoService.salvarPostagemServico( //
                    request.getNomeServico(),
                    request.getTipoServico(),
                    request.getDescricaoServico(),
                    request.getDescricaoPostagem(),
                    cnpjVerificado,
                    request.getFoto()
            );
        }catch (Exception e){
            return ResponseEntity.status(400).body(e.getMessage());
        }

        return ResponseEntity.status(201).body("Postagem criada!");
    }

    @GetMapping(path = "/get",produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getPorCnpj(@RequestParam("cnpj") String cnpj){
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

    @GetMapping(path = "/get",produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getPorTipoServico(@RequestParam("tipo") String tipoServico){
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

    @PutMapping(path = "/edit/{idPostagem}",consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> editarPostagemServico(Principal principal, @PathVariable("idPostagem") Long idPostagem, @RequestBody PostagemRequest request){

        if (principal == null) {
            return ResponseEntity.status(401).body("Usuário não autenticado.");
        }

        try {
            String emailLogado = principal.getName();

            postagemServicoService.editarPostagemServico(emailLogado, idPostagem, request);

        } catch (Exception e) {
            return ResponseEntity.status(403).body("Erro ao editar: " + e.getMessage());
        }

        return ResponseEntity.ok().body("Postagem e Serviço atualizados com sucesso!");
    }
}
