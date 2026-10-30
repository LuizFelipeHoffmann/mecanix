package com.mecanix.controller;

import com.mecanix.config.SessionUtil;
import com.mecanix.dto.AtualizarMembroRequest;
import com.mecanix.dto.CriarMembroRequest;
import com.mecanix.dto.MembroEquipeResponse;
import com.mecanix.dto.UsuarioResponse;
import com.mecanix.exception.BusinessException;
import com.mecanix.service.EquipeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// Gerenciamento de acessos de funcionário (perfil SERVICOS/ESTOQUE), restrito
// ao administrador da empresa. Não entra no mapa ESCRITA do SessionInterceptor
// porque a regra aqui não é "liberado pro ADMIN e mais um perfil" — é
// "liberado só pro ADMIN", inclusive pra leitura, então a verificação é
// feita manualmente em cada método desta classe.
@RestController @RequestMapping("/api/equipe")
public class EquipeController {
    private final EquipeService service;

    public EquipeController(EquipeService service) {
        this.service = service;
    }

    @GetMapping
    public List<MembroEquipeResponse> listar(HttpServletRequest req) {
        exigirAdmin(req);
        return service.listar(SessionUtil.empresaId(req));
    }

    @PostMapping
    public MembroEquipeResponse criar(@Valid @RequestBody CriarMembroRequest body, HttpServletRequest req) {
        exigirAdmin(req);
        return service.criar(SessionUtil.empresaId(req), body);
    }

    @PutMapping("/{id}")
    public MembroEquipeResponse atualizar(@PathVariable Long id, @RequestBody AtualizarMembroRequest body, HttpServletRequest req) {
        exigirAdmin(req);
        return service.atualizar(SessionUtil.empresaId(req), id, body);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> remover(@PathVariable Long id, HttpServletRequest req) {
        exigirAdmin(req);
        service.remover(SessionUtil.empresaId(req), id);
        return Map.of("msg", "Acesso removido");
    }

    private void exigirAdmin(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        UsuarioResponse u = s != null ? (UsuarioResponse) s.getAttribute("usuario") : null;
        if (u == null || !"ADMIN".equals(u.getPerfil()))
            throw new BusinessException("Apenas administradores podem gerenciar a equipe");
    }
}
