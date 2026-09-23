package com.mecanix.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
@Entity @Table(name="itens_servico")
public class ItemServico {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="ordem_id",nullable=false) private OrdemServico ordemServico;
    @Column(nullable=false,length=200) private String descricao;
    @Column(nullable=false,precision=10,scale=2) private BigDecimal valor;
    public Long getId(){return id;} public OrdemServico getOrdemServico(){return ordemServico;}
    public String getDescricao(){return descricao;} public BigDecimal getValor(){return valor;}
    public void setId(Long id){this.id=id;} public void setOrdemServico(OrdemServico o){this.ordemServico=o;}
    public void setDescricao(String d){this.descricao=d;} public void setValor(BigDecimal v){this.valor=v;}
}
