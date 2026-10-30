package com.mecanix.controller;

import com.mecanix.config.SessionUtil;
import com.mecanix.dto.CheckoutRequest;
import com.mecanix.dto.CheckoutResponse;
import com.mecanix.dto.UsuarioResponse;
import com.mecanix.model.Empresa;
import com.mecanix.model.PlanoTipo;
import com.mecanix.exception.BusinessException;
import com.mecanix.repository.EmpresaRepository;
import com.mecanix.service.AssinaturaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/assinaturas")
public class AssinaturaController {
    private final AssinaturaService service;
    private final EmpresaRepository empresaRepo;

    public AssinaturaController(AssinaturaService service, EmpresaRepository empresaRepo) {
        this.service = service;
        this.empresaRepo = empresaRepo;
    }

    // Usado tanto no cadastro (primeira assinatura) quanto na tela de perfil
    // (quando a pessoa troca de plano depois) — em ambos os casos a empresa
    // já existe, então essa mesma rota serve pros dois casos.
    @PostMapping("/checkout")
    public CheckoutResponse checkout(@Valid @RequestBody CheckoutRequest req, HttpServletRequest httpReq) {
        PlanoTipo plano;
        try {
            plano = PlanoTipo.valueOf(req.getPlano().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Plano inválido");
        }

        String cartaoFinal = null;
        if ("CARTAO".equals(req.getFormaPagamento())) {
            String digitos = req.getCartaoNumero() == null ? "" : req.getCartaoNumero().replaceAll("\\D", "");
            if (digitos.length() < 13) throw new BusinessException("Número de cartão inválido");
            cartaoFinal = digitos.substring(digitos.length() - 4);
        }

        Long empresaId = SessionUtil.empresaId(httpReq);
        CheckoutResponse resp = service.criarCheckout(empresaId, plano, req.getGateway(), req.getFormaPagamento(), cartaoFinal);

        // A sessão guarda uma cópia do UsuarioResponse (planoTipo/statusAssinatura/
        // trialFim inclusos) — sem atualizar aqui, quem troca de plano pelo
        // perfil só veria o plano novo depois de deslogar e logar de novo.
        HttpSession session = httpReq.getSession(false);
        Object cached = session != null ? session.getAttribute("usuario") : null;
        if (cached instanceof UsuarioResponse usuario) {
            Empresa empresa = empresaRepo.findById(empresaId).orElse(null);
            if (empresa != null) {
                usuario.setPlanoTipo(empresa.getPlanoTipo() != null ? empresa.getPlanoTipo().name() : null);
                usuario.setStatusAssinatura(empresa.getStatusAssinatura() != null ? empresa.getStatusAssinatura().name() : null);
                usuario.setTrialFim(empresa.getTrialFim() != null ? empresa.getTrialFim().toString() : null);
                session.setAttribute("usuario", usuario);
            }
        }

        return resp;
    }
}
