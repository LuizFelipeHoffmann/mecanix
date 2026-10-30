package com.mecanix.controller;

import com.mecanix.config.SessionUtil;
import com.mecanix.dto.EmailConfigRequest;
import com.mecanix.dto.EmailConfigResponse;
import com.mecanix.dto.UsuarioResponse;
import com.mecanix.exception.BusinessException;
import com.mecanix.service.EmailConfigService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// Configuração da conta de e-mail própria da oficina (aba "E-mail" do
// perfil) — restrito ao administrador, igual ao EquipeController: não entra
// no mapa ESCRITA do SessionInterceptor, então a verificação é manual aqui.
@RestController @RequestMapping("/api/empresa/email")
public class EmailConfigController {
    private final EmailConfigService service;

    public EmailConfigController(EmailConfigService service) {
        this.service = service;
    }

    @GetMapping
    public EmailConfigResponse buscar(HttpServletRequest req) {
        exigirAdmin(req);
        return service.buscar(SessionUtil.empresaId(req));
    }

    @PutMapping
    public EmailConfigResponse atualizar(@RequestBody EmailConfigRequest body, HttpServletRequest req) {
        exigirAdmin(req);
        return service.atualizar(SessionUtil.empresaId(req), body);
    }

    @PostMapping("/testar")
    public Map<String, String> testar(@RequestBody Map<String, String> body, HttpServletRequest req) {
        exigirAdmin(req);
        service.testar(SessionUtil.empresaId(req), body.get("destinatario"));
        return Map.of("msg", "E-mail de teste enviado para " + body.get("destinatario"));
    }

    private void exigirAdmin(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        UsuarioResponse u = s != null ? (UsuarioResponse) s.getAttribute("usuario") : null;
        if (u == null || !"ADMIN".equals(u.getPerfil()))
            throw new BusinessException("Apenas administradores podem configurar o e-mail da oficina");
    }
}
