package com.mecanix.controller;

import com.mecanix.config.SessionUtil;
import com.mecanix.dto.*;
import com.mecanix.service.VeiculoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequestMapping("/api/veiculos")
public class VeiculoController {
    private final VeiculoService service;

    public VeiculoController(VeiculoService service) { this.service = service; }

    @GetMapping
    public List<VeiculoResponse> listar(HttpServletRequest req) { return service.listar(SessionUtil.empresaId(req)); }

    @GetMapping(params = "clienteId")
    public List<VeiculoResponse> listarPorCliente(@RequestParam Long clienteId, HttpServletRequest req) {
        return service.listarPorCliente(clienteId, SessionUtil.empresaId(req));
    }

    @GetMapping("/{id}")
    public VeiculoResponse buscar(@PathVariable Long id, HttpServletRequest req) { return service.buscarPorId(id, SessionUtil.empresaId(req)); }

    @PostMapping
    public ResponseEntity<VeiculoResponse> criar(@Valid @RequestBody VeiculoRequest reqBody, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(reqBody, SessionUtil.empresaId(req)));
    }

    @PutMapping("/{id}")
    public VeiculoResponse atualizar(@PathVariable Long id, @Valid @RequestBody VeiculoRequest reqBody, HttpServletRequest req) {
        return service.atualizar(id, reqBody, SessionUtil.empresaId(req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id, HttpServletRequest req) {
        service.deletar(id, SessionUtil.empresaId(req));
        return ResponseEntity.noContent().build();
    }
}
