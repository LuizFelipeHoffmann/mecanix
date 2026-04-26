package com.mecanix.dto;
import com.mecanix.model.EstoqueItem; import java.math.BigDecimal; import java.util.Set;
public class EstoqueResponse {
    private Long id; private String codigo,nome,categoria; private Integer quantidade,quantidadeMinima; private BigDecimal precoUnitario; private Set<String> tipos; private boolean alertaEstoque;
    public static EstoqueResponse from(EstoqueItem e){
        EstoqueResponse r=new EstoqueResponse();
        r.id=e.getId();r.codigo=e.getCodigo();r.nome=e.getNome();r.categoria=e.getCategoria();
        r.quantidade=e.getQuantidade();r.quantidadeMinima=e.getQuantidadeMinima();r.precoUnitario=e.getPrecoUnitario();
        r.tipos=e.getTipos();r.alertaEstoque=e.isAbaixoMinimo();return r;
    }
    public Long getId(){return id;} public String getCodigo(){return codigo;} public String getNome(){return nome;}
    public String getCategoria(){return categoria;} public Integer getQuantidade(){return quantidade;}
    public Integer getQuantidadeMinima(){return quantidadeMinima;} public BigDecimal getPrecoUnitario(){return precoUnitario;}
    public Set<String> getTipos(){return tipos;} public boolean isAlertaEstoque(){return alertaEstoque;}
}
