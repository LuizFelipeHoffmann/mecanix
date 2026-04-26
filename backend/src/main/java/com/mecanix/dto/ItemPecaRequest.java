package com.mecanix.dto;
import java.math.BigDecimal;
public class ItemPecaRequest {
    private Long estoqueId; private Integer quantidade; private BigDecimal precoUnitario;
    public Long getEstoqueId(){return estoqueId;} public void setEstoqueId(Long id){this.estoqueId=id;}
    public Integer getQuantidade(){return quantidade;} public void setQuantidade(Integer q){this.quantidade=q;}
    public BigDecimal getPrecoUnitario(){return precoUnitario;} public void setPrecoUnitario(BigDecimal p){this.precoUnitario=p;}
}
