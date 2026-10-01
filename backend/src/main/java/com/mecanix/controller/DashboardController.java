package com.mecanix.controller;
import com.mecanix.config.SessionUtil;
import com.mecanix.dto.DashboardResponse;
import com.mecanix.service.OrdemServicoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/dashboard")
public class DashboardController {
    private final OrdemServicoService service;
    public DashboardController(OrdemServicoService service){this.service=service;}
    @GetMapping public DashboardResponse getDashboard(HttpServletRequest req){return service.getDashboard(SessionUtil.empresaId(req));}
}
