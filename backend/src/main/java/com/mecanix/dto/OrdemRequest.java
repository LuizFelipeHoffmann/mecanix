package com.mecanix.dto;
import com.mecanix.model.OrdemServico; import java.time.LocalDate; import java.util.List;
public class OrdemRequest {
    private Long clienteId,veiculoId; private OrdemServico.StatusOS status; private String mecanico,observacoes; private LocalDate data; private List<ItemServicoRequest> servicos; private List<ItemPecaRequest> pecas;
    public Long getClienteId(){return clienteId;} public void setClienteId(Long id){this.clienteId=id;}
    public Long getVeiculoId(){return veiculoId;} public void setVeiculoId(Long id){this.veiculoId=id;}
    public OrdemServico.StatusOS getStatus(){return status;} public void setStatus(OrdemServico.StatusOS s){this.status=s;}
    public String getMecanico(){return mecanico;} public void setMecanico(String m){this.mecanico=m;}
    public String getObservacoes(){return observacoes;} public void setObservacoes(String o){this.observacoes=o;}
    public LocalDate getData(){return data;} public void setData(LocalDate d){this.data=d;}
    public List<ItemServicoRequest> getServicos(){return servicos;} public void setServicos(List<ItemServicoRequest> s){this.servicos=s;}
    public List<ItemPecaRequest> getPecas(){return pecas;} public void setPecas(List<ItemPecaRequest> p){this.pecas=p;}
}
