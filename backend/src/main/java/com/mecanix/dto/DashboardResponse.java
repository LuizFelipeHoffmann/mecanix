package com.mecanix.dto;
import java.math.BigDecimal;
public class DashboardResponse {
    private long ordensAbertas,ordensConcluidas,totalClientes,alertasEstoque; private BigDecimal faturamentoConcluido,ticketMedio;
    public long getOrdensAbertas(){return ordensAbertas;} public void setOrdensAbertas(long v){this.ordensAbertas=v;}
    public long getOrdensConcluidas(){return ordensConcluidas;} public void setOrdensConcluidas(long v){this.ordensConcluidas=v;}
    public long getTotalClientes(){return totalClientes;} public void setTotalClientes(long v){this.totalClientes=v;}
    public long getAlertasEstoque(){return alertasEstoque;} public void setAlertasEstoque(long v){this.alertasEstoque=v;}
    public BigDecimal getFaturamentoConcluido(){return faturamentoConcluido;} public void setFaturamentoConcluido(BigDecimal v){this.faturamentoConcluido=v;}
    public BigDecimal getTicketMedio(){return ticketMedio;} public void setTicketMedio(BigDecimal v){this.ticketMedio=v;}
}
