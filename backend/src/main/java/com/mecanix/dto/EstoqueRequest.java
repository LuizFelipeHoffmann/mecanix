package com.mecanix.dto;
import java.math.BigDecimal; import java.util.Set;
public class EstoqueRequest {
    private String codigo,nome,categoria; private Integer quantidade,quantidadeMinima; private BigDecimal precoUnitario; private Set<String> tipos;
    public String getCodigo(){return codigo;} public void setCodigo(String c){this.codigo=c;}
    public String getNome(){return nome;} public void setNome(String n){this.nome=n;}
    public String getCategoria(){return categoria;} public void setCategoria(String c){this.categoria=c;}
    public Integer getQuantidade(){return quantidade;} public void setQuantidade(Integer q){this.quantidade=q;}
    public Integer getQuantidadeMinima(){return quantidadeMinima;} public void setQuantidadeMinima(Integer q){this.quantidadeMinima=q;}
    public BigDecimal getPrecoUnitario(){return precoUnitario;} public void setPrecoUnitario(BigDecimal p){this.precoUnitario=p;}
    public Set<String> getTipos(){return tipos;} public void setTipos(Set<String> t){this.tipos=t;}
}
