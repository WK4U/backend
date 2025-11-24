package com.workforyou.backend.config;

import com.workforyou.backend.service.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    private boolean isPublic(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();

        if (path == null) return true;
        if ("OPTIONS".equalsIgnoreCase(method)) return true;

        // Rotas públicas de autenticação
        if (path.startsWith("/auth/login")) return true;
        if (path.startsWith("/auth/register")) return true;
        if (path.startsWith("/auth/esqueceu-senha")) return true;
        if (path.startsWith("/auth/validar-pin")) return true;
        if (path.startsWith("/auth/redefinir-senha")) return true;

        // GETs públicos de postagens
        if ("GET".equalsIgnoreCase(method)) {
            if (path.startsWith("/postagem/getAll")) return true;
            if (path.startsWith("/postagem/get")) return true;
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (isPublic(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        String username;
        try {
            username = jwtUtil.extractUsername(jwt);
        } catch (Exception e) {
            log.debug("Token inválido: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = usuarioService.loadUserByUsername(username);
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                log.warn("Falha ao carregar UserDetails para {}: {}", username, e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}