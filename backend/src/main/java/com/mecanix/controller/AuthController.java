package com.mecanix.controller;

import com.mecanix.dto.LoginRequest;
import com.mecanix.dto.UsuarioResponse;
import com.mecanix.exception.BusinessException;
import com.mecanix.service.AuthService;
import jakarta.servlet.http.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController @RequestMapping("/api/auth")
public class AuthController {
    private final AuthService auth;

    public AuthController(AuthService auth) { this.auth = auth; }

    @PostMapping("/login")
    public UsuarioResponse login(@Valid @RequestBody LoginRequest req, HttpServletRequest httpReq) {
        UsuarioResponse resp = auth.autenticar(req.getEmail(), req.getSenha());
        HttpSession session = httpReq.getSession(true);
        session.setAttribute("usuario", resp);
        session.setMaxInactiveInterval(28800);
        return resp;
    }

    @PostMapping("/logout")
    public Map<String, String> logout(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        if (s != null) s.invalidate();
        return Map.of("msg", "Logout realizado");
    }

    @GetMapping("/me")
    public Object me(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        if (s == null) throw new BusinessException("Não autenticado");
        return s.getAttribute("usuario");
    }
}
