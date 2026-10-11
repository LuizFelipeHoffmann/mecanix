package com.mecanix.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// Empresa = a oficina que usa o MECANIX (cliente do sistema, não cliente da oficina).
// Cada Usuario pertence a uma Empresa, e todo dado operacional (Cliente, Veiculo,
// OrdemServico, EstoqueItem) também é vinculado a uma Empresa para isolar os dados
// entre oficinas diferentes que usam o mesmo sistema.
@Entity
@Table(name = "empresas")
@EntityListeners(AuditingEntityListener.class)
public class Empresa {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_fantasia", nullable = false, length = 100)
    private String nomeFantasia;

    @Column(name = "razao_social", nullable = false, length = 150)
    private String razaoSocial;

    @Column(nullable = false, unique = true, length = 18)
    private String cnpj;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String telefone;

    @Column(length = 200)
    private String endereco;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime criadoEm;

    public Long getId() { return id; }
    public String getNomeFantasia() { return nomeFantasia; }
    public String getRazaoSocial() { return razaoSocial; }
    public String getCnpj() { return cnpj; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public String getEndereco() { return endereco; }
    public LocalDateTime getCriadoEm() { return criadoEm; }

    public void setId(Long id) { this.id = id; }
    public void setNomeFantasia(String n) { this.nomeFantasia = n; }
    public void setRazaoSocial(String r) { this.razaoSocial = r; }
    public void setCnpj(String c) { this.cnpj = c; }
    public void setEmail(String e) { this.email = e; }
    public void setTelefone(String t) { this.telefone = t; }
    public void setEndereco(String e) { this.endereco = e; }
}
