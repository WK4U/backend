package com.workforyou.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workforyou.backend.dto.PostagemRequest;
import com.workforyou.backend.model.Postagem;
import com.workforyou.backend.model.Usuario;
import com.workforyou.backend.service.PostagemServicoService;
import com.workforyou.backend.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@RestController
@RequestMapping("/postagem")
@RequiredArgsConstructor
public class PostagemController {

    @Autowired
    private PostagemServicoService postagemServicoService;

    @Autowired
    private UsuarioService usuarioService;


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Validator validator;

    @PostMapping(path = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> register(
            @RequestPart("dados") String dadosJson,
            @RequestPart(value = "file", required = false) MultipartFile file,
            Principal principal
    ) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Usuário não autenticado.");
        }



        PostagemRequest request;
        try {
            request = objectMapper.readValue(dadosJson, PostagemRequest.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("JSON inválido em 'dados': " + e.getMessage());
        }

        var violations = validator.validate(request);
        if (!violations.isEmpty()) {
            String msg = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .reduce((a,b) -> a + "; " + b)
                    .orElse("Dados inválidos");
            return ResponseEntity.badRequest().body(msg);
        }

        try {
            String emailLogado = principal.getName();
            Usuario usuarioLogado = usuarioService.getUsuarioPorEmail(emailLogado);
            String cnpjVerificado = usuarioLogado.getDocumento();

            postagemServicoService.salvarPostagemServico(
//                    request.getNomeServico(),
                    request.getTipoServico(),
//                    request.getDescricaoServico(),
                    request.getDescricaoPostagem(),
                    cnpjVerificado,
                    file
            );
            return ResponseEntity.status(201).body("Postagem criada!");
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
    @PutMapping(path = "/edit/{idPostagem}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> editarPostagemServico(
            Principal principal,
            @PathVariable Long idPostagem,
            @RequestPart("dados") String dadosJson,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Usuário não autenticado.");
        }
        PostagemRequest request;
        try {
            request = objectMapper.readValue(dadosJson, PostagemRequest.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("JSON inválido em 'dados': " + e.getMessage());
        }

        try {
            String emailLogado = principal.getName();
            postagemServicoService.editarPostagemServico(emailLogado, idPostagem, request, file);
        } catch (Exception e) {
            return ResponseEntity.status(403).body("Erro ao editar: " + e.getMessage());
        }

        return ResponseEntity.ok("Postagem e Serviço atualizados com sucesso!");

    }

    @GetMapping(path = "/get/{cnpj}",produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getPorCnpj(@PathVariable("cnpj") String cnpj){
        List<Postagem> postagens;

        try{

            postagens = postagemServicoService.getPostagensPorCnpj(cnpj);

        }catch (Exception e){
            return ResponseEntity.status(404).body(e.getMessage());
        }
        return ResponseEntity.status(200).body(postagens);
    }

    @GetMapping(path = "/getAll", produces = {MediaType.APPLICATION_JSON_VALUE})
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


}
