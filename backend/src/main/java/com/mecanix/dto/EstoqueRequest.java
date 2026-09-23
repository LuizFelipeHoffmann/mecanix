package com.mecanix.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Set;

public class EstoqueRequest {
    @NotBlank(message = "Código é obrigatório")
    private String codigo;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Categoria é obrigatória")
    private String categoria;

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 0, message = "Quantidade não pode ser negativa")
    private Integer quantidade;

    @NotNull(message = "Quantidade mínima é obrigatória")
    @Min(value = 0, message = "Quantidade mínima não pode ser negativa")
    private Integer quantidadeMinima;

    @NotNull(message = "Preço unitário é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Preço deve ser maior que zero")
    private BigDecimal precoUnitario;

    private Set<String> tipos;

    public String getCodigo() { return codigo; }
    public void setCodigo(String c) { this.codigo = c; }
    public String getNome() { return nome; }
    public void setNome(String n) { this.nome = n; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String c) { this.categoria = c; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer q) { this.quantidade = q; }
    public Integer getQuantidadeMinima() { return quantidadeMinima; }
    public void setQuantidadeMinima(Integer q) { this.quantidadeMinima = q; }
    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(BigDecimal p) { this.precoUnitario = p; }
    public Set<String> getTipos() { return tipos; }
    public void setTipos(Set<String> t) { this.tipos = t; }
}
