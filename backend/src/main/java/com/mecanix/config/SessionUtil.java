package com.mecanix.config;

import com.mecanix.dto.UsuarioResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

// Helper pra pegar a empresa do usuário logado a partir da sessão HTTP.
// O SessionInterceptor já garante que existe um "usuario" na sessão pra
// qualquer rota que não seja login/logout/cadastro, então os Controllers
// podem chamar isso direto sem checar null de novo.
public class SessionUtil {
    public static Long empresaId(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        UsuarioResponse u = (UsuarioResponse) s.getAttribute("usuario");
        return u.getEmpresaId();
    }
}
