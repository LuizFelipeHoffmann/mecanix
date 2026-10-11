package com.mecanix.controller;

import com.mecanix.config.SessionUtil;
import com.mecanix.dto.*;
import com.mecanix.service.EstoqueService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequestMapping("/api/estoque")
public class EstoqueController {
    private final EstoqueService service;

    public EstoqueController(EstoqueService service) { this.service = service; }

    @GetMapping
    public List<EstoqueResponse> listar(HttpServletRequest req) { return service.listar(SessionUtil.empresaId(req)); }

    @GetMapping("/alertas")
    public List<EstoqueResponse> alertas(HttpServletRequest req) { return service.listarAlertas(SessionUtil.empresaId(req)); }

    @GetMapping(params = "tipo")
    public List<EstoqueResponse> listarPorTipo(@RequestParam String tipo, HttpServletRequest req) {
        return service.listarPorTipo(tipo, SessionUtil.empresaId(req));
    }

    @GetMapping("/{id}")
    public EstoqueResponse buscar(@PathVariable Long id, HttpServletRequest req) { return service.buscarPorId(id, SessionUtil.empresaId(req)); }

    @PostMapping
    public ResponseEntity<EstoqueResponse> criar(@Valid @RequestBody EstoqueRequest reqBody, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(reqBody, SessionUtil.empresaId(req)));
    }

    @PutMapping("/{id}")
    public EstoqueResponse atualizar(@PathVariable Long id, @Valid @RequestBody EstoqueRequest reqBody, HttpServletRequest req) {
        return service.atualizar(id, reqBody, SessionUtil.empresaId(req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id, HttpServletRequest req) {
        service.deletar(id, SessionUtil.empresaId(req));
        return ResponseEntity.noContent().build();
    }
}
