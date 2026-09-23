package com.mecanix.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ItemServicoRequest {
    @NotBlank(message = "Descrição do serviço é obrigatória")
    private String descricao;

    @NotNull(message = "Valor do serviço é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Valor deve ser maior que zero")
    private BigDecimal valor;

    public String getDescricao() { return descricao; }
    public void setDescricao(String d) { this.descricao = d; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal v) { this.valor = v; }
}
