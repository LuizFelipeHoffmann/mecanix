package com.mecanix.controller;

import com.mecanix.dto.OrdemRequest;
import com.mecanix.dto.OrdemResponse;
import com.mecanix.exception.BusinessException;
import com.mecanix.service.EmailService;
import com.mecanix.service.OrdemServicoService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController @RequestMapping("/api/ordens")
public class OrdemController {
    private final OrdemServicoService service;
    private final EmailService emailService;

    public OrdemController(OrdemServicoService service, EmailService emailService) {
        this.service = service;
        this.emailService = emailService;
    }

    @GetMapping
    public List<OrdemResponse> listar() { return service.listar(); }

    @GetMapping(params = "status")
    public List<OrdemResponse> listarPorStatus(@RequestParam String status) {
        return service.listarPorStatus(status);
    }

    @GetMapping("/{id}")
    public OrdemResponse buscar(@PathVariable Long id) { return service.buscarPorId(id); }

    @PostMapping
    public ResponseEntity<OrdemResponse> criar(@Valid @RequestBody OrdemRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(req));
    }

    @PutMapping("/{id}")
    public OrdemResponse atualizar(@PathVariable Long id, @Valid @RequestBody OrdemRequest req) {
        return service.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/enviar-email")
    public ResponseEntity<Map<String, String>> enviarEmail(@PathVariable Long id) {
        OrdemResponse os = service.buscarPorId(id);
        if (os.getClienteEmail() == null || os.getClienteEmail().isBlank())
            throw new BusinessException("O cliente não possui e-mail cadastrado");
        try {
            emailService.enviarOS(os, os.getClienteEmail());
            return ResponseEntity.ok(Map.of("msg", "E-mail enviado para " + os.getClienteEmail()));
        } catch (Exception e) {
            throw new BusinessException("Erro ao enviar e-mail: " + e.getMessage());
        }
    }
}
