package com.mecanix.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "clientes", uniqueConstraints = @UniqueConstraint(columnNames = {"empresa_id", "cpf"}))
@EntityListeners(AuditingEntityListener.class)
public class Cliente {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 14)
    private String cpf;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String telefone;

    @Column(length = 9)
    private String cep;

    @Column(length = 150)
    private String rua;

    @Column(length = 20)
    private String numero;

    @Column(length = 80)
    private String bairro;

    @Column(length = 80)
    private String cidade;

    @Column(length = 2)
    private String uf;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime criadoEm;

    @LastModifiedDate
    private LocalDateTime atualizadoEm;

    @JsonIgnore
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Veiculo> veiculos;

    @JsonIgnore
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdemServico> ordens;

    public Long getId() { return id; }
    public Empresa getEmpresa() { return empresa; }
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public String getCep() { return cep; }
    public String getRua() { return rua; }
    public String getNumero() { return numero; }
    public String getBairro() { return bairro; }
    public String getCidade() { return cidade; }
    public String getUf() { return uf; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public List<Veiculo> getVeiculos() { return veiculos; }
    public List<OrdemServico> getOrdens() { return ordens; }

    public void setId(Long id) { this.id = id; }
    public void setEmpresa(Empresa e) { this.empresa = e; }
    public void setNome(String n) { this.nome = n; }
    public void setCpf(String c) { this.cpf = c; }
    public void setEmail(String e) { this.email = e; }
    public void setTelefone(String t) { this.telefone = t; }
    public void setCep(String c) { this.cep = c; }
    public void setRua(String r) { this.rua = r; }
    public void setNumero(String n) { this.numero = n; }
    public void setBairro(String b) { this.bairro = b; }
    public void setCidade(String c) { this.cidade = c; }
    public void setUf(String u) { this.uf = u; }
}
