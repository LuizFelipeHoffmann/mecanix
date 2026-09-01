package com.mecanix.config;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mecanix.dto.UsuarioResponse;
import jakarta.servlet.http.*;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.Map;
import java.util.Set;

public class SessionInterceptor implements HandlerInterceptor {
    private final ObjectMapper mapper = new ObjectMapper();

    // Perfis autorizados a ESCREVER em cada módulo. Leitura (GET) é liberada a
    // qualquer usuário autenticado — o Dashboard mostra OS e alertas de estoque
    // para os três perfis.
    private static final Map<String, Set<String>> ESCRITA = Map.of(
        "/api/clientes", Set.of("ADMIN", "SERVICOS"),
        "/api/veiculos", Set.of("ADMIN", "SERVICOS"),
        "/api/ordens",   Set.of("ADMIN", "SERVICOS"),
        "/api/estoque",  Set.of("ADMIN", "ESTOQUE")
    );

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        // Permite requisições OPTIONS (preflight CORS) sem verificar sessão
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            return true;
        }
        HttpSession session = req.getSession(false);
        Object usuario = session != null ? session.getAttribute("usuario") : null;
        if (usuario == null) {
            return recusar(res, HttpServletResponse.SC_UNAUTHORIZED, "Não autenticado");
        }
        if ("GET".equalsIgnoreCase(req.getMethod())) {
            return true;
        }
        String perfil = ((UsuarioResponse) usuario).getPerfil();
        for (Map.Entry<String, Set<String>> e : ESCRITA.entrySet()) {
            if (req.getRequestURI().startsWith(e.getKey()) && !e.getValue().contains(perfil)) {
                return recusar(res, HttpServletResponse.SC_FORBIDDEN,
                    "Seu perfil não tem permissão para esta operação");
            }
        }
        return true;
    }

    private boolean recusar(HttpServletResponse res, int status, String msg) throws Exception {
        res.setStatus(status);
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write(mapper.writeValueAsString(Map.of("status", status, "erro", msg)));
        return false;
    }
}
