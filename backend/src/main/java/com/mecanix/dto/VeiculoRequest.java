package com.mecanix.dto;

import com.mecanix.model.Veiculo;
import jakarta.validation.constraints.*;

public class VeiculoRequest {
    @NotNull(message = "Cliente é obrigatório")
    private Long clienteId;

    @NotBlank(message = "Marca é obrigatória")
    private String marca;

    @NotBlank(message = "Modelo é obrigatório")
    private String modelo;

    @NotBlank(message = "Ano é obrigatório")
    @Pattern(regexp = "\\d{4}", message = "Ano inválido")
    private String ano;

    private String cor;

    @NotBlank(message = "Placa é obrigatória")
    @Pattern(regexp = "[A-Z]{3}\\d[A-Z0-9]\\d{2}|[A-Z]{3}-?\\d{4}", message = "Placa inválida")
    private String placa;

    @Min(value = 0, message = "Quilometragem não pode ser negativa")
    private Integer quilometragem;

    @NotNull(message = "Tipo de veículo é obrigatório")
    private Veiculo.TipoVeiculo tipo;

    private String observacoes;

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long id) { this.clienteId = id; }
    public String getMarca() { return marca; }
    public void setMarca(String m) { this.marca = m; }
    public String getModelo() { return modelo; }
    public void setModelo(String m) { this.modelo = m; }
    public String getAno() { return ano; }
    public void setAno(String a) { this.ano = a; }
    public String getCor() { return cor; }
    public void setCor(String c) { this.cor = c; }
    public String getPlaca() { return placa; }
    public void setPlaca(String p) { this.placa = p; }
    public Integer getQuilometragem() { return quilometragem; }
    public void setQuilometragem(Integer k) { this.quilometragem = k; }
    public Veiculo.TipoVeiculo getTipo() { return tipo; }
    public void setTipo(Veiculo.TipoVeiculo t) { this.tipo = t; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String o) { this.observacoes = o; }
}
