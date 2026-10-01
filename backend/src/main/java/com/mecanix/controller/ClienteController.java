package com.mecanix.controller;

import com.mecanix.config.SessionUtil;
import com.mecanix.dto.*;
import com.mecanix.service.ClienteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequestMapping("/api/clientes")
public class ClienteController {
    private final ClienteService service;

    public ClienteController(ClienteService service) { this.service = service; }

    @GetMapping
    public List<ClienteResponse> listar(HttpServletRequest req) { return service.listar(SessionUtil.empresaId(req)); }

    @GetMapping("/{id}")
    public ClienteResponse buscar(@PathVariable Long id, HttpServletRequest req) { return service.buscarPorId(id, SessionUtil.empresaId(req)); }

    @PostMapping
    public ResponseEntity<ClienteResponse> criar(@Valid @RequestBody ClienteRequest reqBody, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(reqBody, SessionUtil.empresaId(req)));
    }

    @PutMapping("/{id}")
    public ClienteResponse atualizar(@PathVariable Long id, @Valid @RequestBody ClienteRequest reqBody, HttpServletRequest req) {
        return service.atualizar(id, reqBody, SessionUtil.empresaId(req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id, HttpServletRequest req) {
        service.deletar(id, SessionUtil.empresaId(req));
        return ResponseEntity.noContent().build();
    }
}
