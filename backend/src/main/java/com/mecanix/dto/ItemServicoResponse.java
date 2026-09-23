package com.mecanix.dto;
import java.math.BigDecimal;
public class ItemServicoResponse {
    private Long id; private String descricao; private BigDecimal valor;
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getDescricao(){return descricao;} public void setDescricao(String d){this.descricao=d;}
    public BigDecimal getValor(){return valor;} public void setValor(BigDecimal v){this.valor=v;}
}
