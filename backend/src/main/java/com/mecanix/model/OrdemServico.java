package com.mecanix.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Entity @Table(name="ordens_servico")
public class OrdemServico {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="cliente_id",nullable=false) private Cliente cliente;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="veiculo_id",nullable=false) private Veiculo veiculo;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private StatusOS status;
    @Column(length=80) private String mecanico;
    @Column(nullable=false) private LocalDate data;
    @Column(length=500) private String observacoes;
    @OneToMany(mappedBy="ordemServico",cascade=CascadeType.ALL,orphanRemoval=true)
    private List<ItemServico> servicos=new ArrayList<>();
    @OneToMany(mappedBy="ordemServico",cascade=CascadeType.ALL,orphanRemoval=true)
    private List<ItemPeca> pecas=new ArrayList<>();
    public enum StatusOS { ANDAMENTO, AGUARDANDO, AGENDADO, CONCLUIDO, CANCELADO }
    public BigDecimal getTotalServicos(){BigDecimal t=BigDecimal.ZERO;for(ItemServico s:servicos)t=t.add(s.getValor());return t;}
    public BigDecimal getTotalPecas(){BigDecimal t=BigDecimal.ZERO;for(ItemPeca p:pecas)t=t.add(p.getPrecoUnitario().multiply(BigDecimal.valueOf(p.getQuantidade())));return t;}
    public BigDecimal getTotal(){return getTotalServicos().add(getTotalPecas());}
    public Long getId(){return id;} public Cliente getCliente(){return cliente;}
    public Veiculo getVeiculo(){return veiculo;} public StatusOS getStatus(){return status;}
    public String getMecanico(){return mecanico;} public LocalDate getData(){return data;}
    public String getObservacoes(){return observacoes;} public List<ItemServico> getServicos(){return servicos;}
    public List<ItemPeca> getPecas(){return pecas;}
    public void setId(Long id){this.id=id;} public void setCliente(Cliente c){this.cliente=c;}
    public void setVeiculo(Veiculo v){this.veiculo=v;} public void setStatus(StatusOS s){this.status=s;}
    public void setMecanico(String m){this.mecanico=m;} public void setData(LocalDate d){this.data=d;}
    public void setObservacoes(String o){this.observacoes=o;} public void setServicos(List<ItemServico> s){this.servicos=s;}
    public void setPecas(List<ItemPeca> p){this.pecas=p;}
}
