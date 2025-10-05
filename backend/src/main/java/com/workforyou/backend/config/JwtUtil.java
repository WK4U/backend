package com.workforyou.backend.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails; // Adicionar esta importação
import org.springframework.stereotype.Component; // CRÍTICO
import java.util.Date;
import java.util.function.Function;

@Component // CRÍTICO: Torna a classe um Bean para ser injetada no JwtFilter
public class JwtUtil {

    // Sua chave secreta original
    private static final String SECRET = "chave_secreta_para_jwt";

    // Mantido estático para ser usado no AuthController, mas pode ser de instância se preferir
    public static String gerarToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 dia
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    // Método de instância usado pelo filtro: Extrai o username (o Subject/Email)
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Método de instância usado pelo filtro: Valida se o token é válido
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        // Verifica se o username bate e se o token não está expirado
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Métodos auxiliares
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}
