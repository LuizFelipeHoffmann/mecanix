package com.mecanix.controller;

import com.mecanix.config.SessionUtil;
import com.mecanix.dto.CadastroRequest;
import com.mecanix.dto.LoginRequest;
import com.mecanix.dto.PerfilRequest;
import com.mecanix.dto.SenhaRequest;
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

    @PostMapping("/cadastro")
    public UsuarioResponse cadastro(@Valid @RequestBody CadastroRequest req, HttpServletRequest httpReq) {
        UsuarioResponse resp = auth.cadastrarEmpresa(req);
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

    // Qualquer perfil (ADMIN/SERVICOS/ESTOQUE) pode editar o próprio nome/foto
    // — não é um recurso restrito por módulo, então não entra no mapa ESCRITA
    // do SessionInterceptor, só exige estar logado.
    @PutMapping("/perfil")
    public UsuarioResponse atualizarPerfil(@RequestBody PerfilRequest req, HttpServletRequest httpReq) {
        UsuarioResponse resp = auth.atualizarPerfil(SessionUtil.usuarioId(httpReq), req);
        // a sessão guarda uma cópia do UsuarioResponse (pra não bater no banco
        // em cada request) — precisa atualizar aqui senão o nome/foto só
        // apareceriam de novo depois de deslogar e logar
        httpReq.getSession(false).setAttribute("usuario", resp);
        return resp;
    }

    @PutMapping("/senha")
    public Map<String, String> alterarSenha(@Valid @RequestBody SenhaRequest req, HttpServletRequest httpReq) {
        auth.alterarSenha(SessionUtil.usuarioId(httpReq), req);
        return Map.of("msg", "Senha alterada com sucesso");
    }
}
