package com.workforyou.backend.config;

// Importações CRÍTICAS para o Spring Security
import com.workforyou.backend.service.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder; // PASSO 3 CRÍTICO
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter; // PASSO 1 CRÍTICO
import java.io.IOException;

@Component
@RequiredArgsConstructor // Necessário para injetar as final fields (UsuarioService e JwtUtil)
public class JwtFilter extends OncePerRequestFilter {

    // PASSO 2 CRÍTICO: Injeção de dependências
    @Autowired
    private final UsuarioService usuarioService;

    private final JwtUtil jwtUtil; // Assumindo que você corrigiu o JwtUtil para ser @Component

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        // 1. Extrair o Token do Header
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                // Tenta extrair o username (e-mail)
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                // Se der erro (token expirado/inválido), apenas logamos e username será null
                logger.warn("Falha ao extrair username do token ou token inválido.", e);
            }
        }

        // 2. Autenticar no Contexto do Spring Security
        // Checa se extraímos o username E se o usuário ainda não está autenticado na sessão
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Carrega o UserDetails (PASSO CRÍTICO - usa seu UsuarioService corrigido)
            UserDetails userDetails = this.usuarioService.loadUserByUsername(username);

            // 3. Valida e Configura a Autenticação
            if (jwtUtil.validateToken(jwt, userDetails)) {

                // Cria o objeto de autenticação que o Spring entende
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                // Define detalhes da requisição
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 4. PASSO FINAL: Coloca o usuário no contexto! (Resolve o 403 Forbidden)
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continua a cadeia de filtros.
        filterChain.doFilter(request, response);
    }

    // Método para ignorar o filtro em rotas públicas (como a sua /auth/**)
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // Isso é mais robusto que verificar getServletPath() diretamente, como você tinha
        return request.getServletPath().startsWith("/auth/");
    }
}
