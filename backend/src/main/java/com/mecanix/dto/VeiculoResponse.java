package com.mecanix.dto;
import com.mecanix.model.Veiculo;
public class VeiculoResponse {
    private Long id,clienteId; private String clienteNome,marca,modelo,ano,cor,placa,tipo,observacoes; private Integer quilometragem; private int totalOrdens;
    public static VeiculoResponse from(Veiculo v){
        VeiculoResponse r=new VeiculoResponse();
        r.id=v.getId();r.clienteId=v.getCliente().getId();r.clienteNome=v.getCliente().getNome();
        r.marca=v.getMarca();r.modelo=v.getModelo();r.ano=v.getAno();r.cor=v.getCor();r.placa=v.getPlaca();
        r.quilometragem=v.getQuilometragem();r.tipo=v.getTipo().name();r.observacoes=v.getObservacoes();
        r.totalOrdens=v.getOrdens()!=null?v.getOrdens().size():0;return r;
    }
    public Long getId(){return id;} public Long getClienteId(){return clienteId;} public String getClienteNome(){return clienteNome;}
    public String getMarca(){return marca;} public String getModelo(){return modelo;} public String getAno(){return ano;}
    public String getCor(){return cor;} public String getPlaca(){return placa;} public String getTipo(){return tipo;}
    public String getObservacoes(){return observacoes;} public Integer getQuilometragem(){return quilometragem;} public int getTotalOrdens(){return totalOrdens;}
}
