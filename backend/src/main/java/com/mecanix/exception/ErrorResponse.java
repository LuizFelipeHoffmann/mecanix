package com.mecanix.exception;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponse {
    private int status;
    private String erro;
    private List<String> detalhes;
    private LocalDateTime timestamp;

    public ErrorResponse(int status, String erro) {
        this.status = status;
        this.erro = erro;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(int status, String erro, List<String> detalhes) {
        this(status, erro);
        this.detalhes = detalhes;
    }

    public int getStatus() { return status; }
    public String getErro() { return erro; }
    public List<String> getDetalhes() { return detalhes; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
