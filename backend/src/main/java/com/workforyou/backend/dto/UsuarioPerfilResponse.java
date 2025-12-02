package com.workforyou.backend.dto;

import com.workforyou.backend.model.Cliente;
import com.workforyou.backend.model.Prestador;
import com.workforyou.backend.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioPerfilResponse {
    private Long id;
    private String nome;
    private String email;
    private String cpf;
    private String cnpj;
    private String foto;
    private String telefone;
    private LocalDate dataNascimento; // O campo já existia aqui

    public static UsuarioPerfilResponse from(
            Usuario usuario,
            Cliente cliente,
            Prestador prestador
    ) {
        if (usuario == null) return null;

        String tipo = String.valueOf(usuario.getTipoUsuario()).trim();
        boolean isCliente = "F".equalsIgnoreCase(tipo);
        boolean isPrestador = "J".equalsIgnoreCase(tipo);

        String foto = null;
        String nome = null;
        String cpf = null;
        String cnpj = null;
        String telefone  = null;
        LocalDate dataNascimento = null; // 1. CRIEI A VARIÁVEL AQUI

        if (isCliente && cliente != null) {
            foto = cliente.getUrlFoto();
            if (cliente.getPessoaFisica() != null) {
                nome = cliente.getPessoaFisica().getNome();
                cpf = cliente.getPessoaFisica().getCpf();
                telefone = cliente.getPessoaFisica().getTelefone();

                // 2. PREENCHI A VARIÁVEL AQUI
                dataNascimento = cliente.getPessoaFisica().getDataNascimento();
            }
        }

        if (isPrestador && prestador != null) {
            foto = prestador.getUrlFoto();
            if (prestador.getPessoaJuridica() != null) {
                nome = prestador.getPessoaJuridica().getNome();
                cnpj = prestador.getPessoaJuridica().getCnpj();
                telefone = prestador.getPessoaJuridica().getTelefone();
            }
        }

        // fallbacks
        if (nome == null || nome.isBlank()) nome = usuario.getEmail();
        if (cpf == null && cnpj == null) {
            if ("F".equalsIgnoreCase(tipo)) cpf = usuario.getDocumento();
            else cnpj = usuario.getDocumento();
        }

        return UsuarioPerfilResponse.builder()
                .id(usuario.getId())
                .nome(nome)
                .email(usuario.getEmail())
                .cpf(cpf)
                .cnpj(cnpj)
                .telefone(telefone)
                .foto(foto)
                .dataNascimento(dataNascimento) // 3. MANDEI PRO JSON AQUI
                .build();
    }
}