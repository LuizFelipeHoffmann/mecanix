package com.mecanix.dto;
import java.math.BigDecimal; import java.time.LocalDate; import java.util.List;
public class OrdemResponse {
    private Long id,clienteId,veiculoId; private String clienteNome,clienteEmail,veiculoDesc,veiculoPlaca,status,mecanico,observacoes; private LocalDate data; private List<ItemServicoResponse> servicos; private List<ItemPecaResponse> pecas; private BigDecimal totalServicos,totalPecas,total;
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public Long getClienteId(){return clienteId;} public void setClienteId(Long id){this.clienteId=id;}
    public Long getVeiculoId(){return veiculoId;} public void setVeiculoId(Long id){this.veiculoId=id;}
    public String getClienteNome(){return clienteNome;} public void setClienteNome(String s){this.clienteNome=s;}
    public String getClienteEmail(){return clienteEmail;} public void setClienteEmail(String s){this.clienteEmail=s;}
    public String getVeiculoDesc(){return veiculoDesc;} public void setVeiculoDesc(String s){this.veiculoDesc=s;}
    public String getVeiculoPlaca(){return veiculoPlaca;} public void setVeiculoPlaca(String s){this.veiculoPlaca=s;}
    public String getStatus(){return status;} public void setStatus(String s){this.status=s;}
    public String getMecanico(){return mecanico;} public void setMecanico(String s){this.mecanico=s;}
    public String getObservacoes(){return observacoes;} public void setObservacoes(String s){this.observacoes=s;}
    public LocalDate getData(){return data;} public void setData(LocalDate d){this.data=d;}
    public List<ItemServicoResponse> getServicos(){return servicos;} public void setServicos(List<ItemServicoResponse> s){this.servicos=s;}
    public List<ItemPecaResponse> getPecas(){return pecas;} public void setPecas(List<ItemPecaResponse> p){this.pecas=p;}
    public BigDecimal getTotalServicos(){return totalServicos;} public void setTotalServicos(BigDecimal v){this.totalServicos=v;}
    public BigDecimal getTotalPecas(){return totalPecas;} public void setTotalPecas(BigDecimal v){this.totalPecas=v;}
    public BigDecimal getTotal(){return total;} public void setTotal(BigDecimal v){this.total=v;}
}
