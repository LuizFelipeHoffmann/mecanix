package com.mecanix.controller;

import com.mecanix.dto.PlanoResponse;
import com.mecanix.service.PlanoService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// Pública (sem sessão — ver exclusão em WebConfig) — usada na tela de
// cadastro, antes de existir conta/sessão.
@RestController @RequestMapping("/api/planos")
public class PlanoController {
    private final PlanoService service;
    public PlanoController(PlanoService service) { this.service = service; }

    @GetMapping
    public List<PlanoResponse> listar() { return service.listar(); }
}
