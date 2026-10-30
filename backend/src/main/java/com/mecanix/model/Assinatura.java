package com.mecanix.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// Histórico de cobranças/assinaturas de uma Empresa num gateway de pagamento.
// Fica numa tabela própria (em vez de colunas soltas na Empresa) porque uma
// oficina pode trocar de plano ou de gateway ao longo do tempo — cada linha
// aqui é uma tentativa/ciclo de assinatura, não o estado atual (esse fica em
// Empresa.statusAssinatura, atualizado quando o webhook confirma o pagamento).
@Entity
@Table(name = "assinaturas")
public class Assinatura {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Enumerated(EnumType.STRING) @Column(name = "plano_tipo", length = 20)
    private PlanoTipo planoTipo;

    @Column(length = 20)
    private String gateway; // "mercadopago" | "asaas"

    @Column(name = "referencia_externa", length = 100)
    private String referenciaExterna; // id da preapproval/subscription no gateway

    @Enumerated(EnumType.STRING) @Column(length = 20)
    private StatusAssinatura status;

    @Column(precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "forma_pagamento", length = 10)
    private String formaPagamento; // "CARTAO" | "PIX"

    // só os 4 últimos dígitos, pra exibir "Cartão final 1234" — número
    // completo e CVV nunca são persistidos (ver CheckoutRequest).
    @Column(name = "cartao_final", length = 4)
    private String cartaoFinal;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column(name = "data_fim")
    private LocalDate dataFim;

    public Long getId() { return id; }
    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa e) { this.empresa = e; }
    public PlanoTipo getPlanoTipo() { return planoTipo; }
    public void setPlanoTipo(PlanoTipo p) { this.planoTipo = p; }
    public String getGateway() { return gateway; }
    public void setGateway(String g) { this.gateway = g; }
    public String getReferenciaExterna() { return referenciaExterna; }
    public void setReferenciaExterna(String r) { this.referenciaExterna = r; }
    public StatusAssinatura getStatus() { return status; }
    public void setStatus(StatusAssinatura s) { this.status = s; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal v) { this.valor = v; }
    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String f) { this.formaPagamento = f; }
    public String getCartaoFinal() { return cartaoFinal; }
    public void setCartaoFinal(String c) { this.cartaoFinal = c; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate d) { this.dataFim = d; }
}
