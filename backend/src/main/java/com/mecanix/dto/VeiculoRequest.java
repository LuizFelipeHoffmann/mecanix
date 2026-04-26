package com.mecanix.dto;
import com.mecanix.model.Veiculo;
public class VeiculoRequest {
    private Long clienteId; private String marca,modelo,ano,cor,placa,observacoes; private Integer quilometragem; private Veiculo.TipoVeiculo tipo;
    public Long getClienteId(){return clienteId;} public void setClienteId(Long id){this.clienteId=id;}
    public String getMarca(){return marca;} public void setMarca(String m){this.marca=m;}
    public String getModelo(){return modelo;} public void setModelo(String m){this.modelo=m;}
    public String getAno(){return ano;} public void setAno(String a){this.ano=a;}
    public String getCor(){return cor;} public void setCor(String c){this.cor=c;}
    public String getPlaca(){return placa;} public void setPlaca(String p){this.placa=p;}
    public String getObservacoes(){return observacoes;} public void setObservacoes(String o){this.observacoes=o;}
    public Integer getQuilometragem(){return quilometragem;} public void setQuilometragem(Integer k){this.quilometragem=k;}
    public Veiculo.TipoVeiculo getTipo(){return tipo;} public void setTipo(Veiculo.TipoVeiculo t){this.tipo=t;}
}
