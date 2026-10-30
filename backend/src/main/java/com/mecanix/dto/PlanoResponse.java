package com.mecanix.dto;

import com.mecanix.model.PlanoTipo;
import java.math.BigDecimal;
import java.util.List;

public class PlanoResponse {
    private PlanoTipo tipo;
    private String nome;
    private BigDecimal precoCobranca;          // valor cobrado a cada ciclo (após o período grátis, se houver)
    private int cicloMeses;                    // 1 = cobrança mensal, 12 = cobrança anual
    private int trialMeses;                    // meses sem cobrança antes da primeira cobrança (só no plano Grátis)
    private BigDecimal precoEquivalenteMensal; // pra comparar visualmente com o mensal (preço anual ÷ 12)
    private List<String> destaques;

    public PlanoResponse() {}
    public PlanoResponse(PlanoTipo tipo, String nome, BigDecimal precoCobranca, int cicloMeses,
                          int trialMeses, BigDecimal precoEquivalenteMensal, List<String> destaques) {
        this.tipo = tipo; this.nome = nome; this.precoCobranca = precoCobranca;
        this.cicloMeses = cicloMeses; this.trialMeses = trialMeses;
        this.precoEquivalenteMensal = precoEquivalenteMensal; this.destaques = destaques;
    }
    public PlanoTipo getTipo() { return tipo; }
    public void setTipo(PlanoTipo t) { this.tipo = t; }
    public String getNome() { return nome; }
    public void setNome(String n) { this.nome = n; }
    public BigDecimal getPrecoCobranca() { return precoCobranca; }
    public void setPrecoCobranca(BigDecimal p) { this.precoCobranca = p; }
    public int getCicloMeses() { return cicloMeses; }
    public void setCicloMeses(int c) { this.cicloMeses = c; }
    public int getTrialMeses() { return trialMeses; }
    public void setTrialMeses(int t) { this.trialMeses = t; }
    public BigDecimal getPrecoEquivalenteMensal() { return precoEquivalenteMensal; }
    public void setPrecoEquivalenteMensal(BigDecimal p) { this.precoEquivalenteMensal = p; }
    public List<String> getDestaques() { return destaques; }
    public void setDestaques(List<String> d) { this.destaques = d; }
}
