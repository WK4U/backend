package com.workforyou.backend.config;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class JwtFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getServletPath();

        // Ignorar endpoints públicos (login, registro, etc.)
        if (path.startsWith("/auth/")) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = req.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                JwtUtil.validarToken(token); // se inválido lança exceção
            } catch (Exception e) {
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido ou expirado");
                return;
            }
        } else {
            // Se não tiver token e não for endpoint público
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso negado: Token não fornecido");
            return;
        }

        chain.doFilter(request, response);
    }
}


