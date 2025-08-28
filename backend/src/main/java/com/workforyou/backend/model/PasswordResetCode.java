package com.workforyou.backend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class PasswordResetCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private LocalDateTime expiracao;

    private boolean tempo;

    @ManyToOne
    private Usuario usuario;

    public PasswordResetCode(){
    }

    public PasswordResetCode(Long id, String code, LocalDateTime expiracao, boolean tempo, Usuario usuario) {
        this.id = id;
        this.code = code;
        this.expiracao = expiracao;
        this.tempo = tempo;
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getExpiracao() {
        return expiracao;
    }

    public void setExpiracao(LocalDateTime expiracao) {
        this.expiracao = expiracao;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public boolean isUsado() {
        return tempo;
    }

    public void setUsado(boolean usado) {
        this.tempo = tempo;
    }
}


