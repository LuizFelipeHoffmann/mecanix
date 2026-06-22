package com.mecanix.controller;

import com.mecanix.dto.*;
import com.mecanix.service.VeiculoService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequestMapping("/api/veiculos")
public class VeiculoController {
    private final VeiculoService service;

    public VeiculoController(VeiculoService service) { this.service = service; }

    @GetMapping
    public List<VeiculoResponse> listar() { return service.listar(); }

    @GetMapping(params = "clienteId")
    public List<VeiculoResponse> listarPorCliente(@RequestParam Long clienteId) {
        return service.listarPorCliente(clienteId);
    }

    @GetMapping("/{id}")
    public VeiculoResponse buscar(@PathVariable Long id) { return service.buscarPorId(id); }

    @PostMapping
    public ResponseEntity<VeiculoResponse> criar(@Valid @RequestBody VeiculoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(req));
    }

    @PutMapping("/{id}")
    public VeiculoResponse atualizar(@PathVariable Long id, @Valid @RequestBody VeiculoRequest req) {
        return service.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
