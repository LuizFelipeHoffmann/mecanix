package com.mecanix.config;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.*;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.Map;

public class SessionInterceptor implements HandlerInterceptor {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        // Permite requisições OPTIONS (preflight CORS) sem verificar sessão
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            return true;
        }
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("usuario") != null) {
            return true;
        }
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write(mapper.writeValueAsString(Map.of("erro", "Não autenticado")));
        return false;
    }
}
