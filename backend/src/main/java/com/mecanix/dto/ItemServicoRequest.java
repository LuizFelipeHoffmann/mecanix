package com.mecanix.dto;
import java.math.BigDecimal;
public class ItemServicoRequest {
    private String descricao; private BigDecimal valor;
    public String getDescricao(){return descricao;} public void setDescricao(String d){this.descricao=d;}
    public BigDecimal getValor(){return valor;} public void setValor(BigDecimal v){this.valor=v;}
}
