package com.mecanix.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
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

    // CNPJ ou CPF (oficina de pessoa física/MEI sem CNPJ ainda).
    // nullable (mesmo motivo do plano_tipo/status_assinatura acima): ddl-auto=update
    // não consegue adicionar coluna NOT NULL numa tabela que já tem linhas — o default
    // "CNPJ" fica só no lado Java, linhas antigas sem valor são tratadas como CNPJ.
    @Column(name = "tipo_documento", length = 4)
    private String tipoDocumento = "CNPJ";

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

    // Plano/assinatura — colunas nullable de propósito: ddl-auto=update não
    // lida bem com NOT NULL em tabela que já tem linhas, então o default fica
    // só no lado Java (linhas antigas sem valor são tratadas como GRATIS/TRIAL
    // no código). Histórico de cobranças fica em Assinatura, não aqui.
    @Enumerated(EnumType.STRING) @Column(name = "plano_tipo", length = 20)
    private PlanoTipo planoTipo = PlanoTipo.GRATIS;

    @Enumerated(EnumType.STRING) @Column(name = "status_assinatura", length = 20)
    private StatusAssinatura statusAssinatura = StatusAssinatura.TRIAL;

    @Column(name = "trial_fim")
    private LocalDate trialFim;

    // Conta de e-mail PRÓPRIA da oficina pra enviar as OS pro cliente (em vez
    // da conta compartilhada do MECANIX) — configurado na aba "E-mail" do
    // perfil. Os três ficam nulos até a oficina configurar; nesse caso o
    // envio cai no fallback da conta compartilhada (ver EmailService).
    @Column(name = "email_remetente_proprio", length = 150)
    private String emailRemetenteProprio;

    // Senha de app (ex: Gmail) CRIPTOGRAFADA (AES-GCM, ver CryptoUtil) — nunca
    // fica em texto puro no banco. TEXT porque o texto cifrado em base64 é
    // maior que a senha original.
    @Column(name = "email_senha_proprio", columnDefinition = "TEXT")
    private String emailSenhaProprio;

    // Nome exibido como remetente quando envia pela conta própria (ex: "Auto
    // Center do João"). Se nulo, cai no nomeFantasia na hora de montar o e-mail.
    @Column(name = "email_nome_remetente", length = 100)
    private String emailNomeRemetente;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime criadoEm;

    public Long getId() { return id; }
    public String getNomeFantasia() { return nomeFantasia; }
    public String getRazaoSocial() { return razaoSocial; }
    public String getCnpj() { return cnpj; }
    public String getTipoDocumento() { return tipoDocumento; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public String getCep() { return cep; }
    public String getRua() { return rua; }
    public String getNumero() { return numero; }
    public String getBairro() { return bairro; }
    public String getCidade() { return cidade; }
    public String getUf() { return uf; }
    public PlanoTipo getPlanoTipo() { return planoTipo; }
    public StatusAssinatura getStatusAssinatura() { return statusAssinatura; }
    public LocalDate getTrialFim() { return trialFim; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public String getEmailRemetenteProprio() { return emailRemetenteProprio; }
    public String getEmailSenhaProprio() { return emailSenhaProprio; }
    public String getEmailNomeRemetente() { return emailNomeRemetente; }

    public void setId(Long id) { this.id = id; }
    public void setNomeFantasia(String n) { this.nomeFantasia = n; }
    public void setRazaoSocial(String r) { this.razaoSocial = r; }
    public void setCnpj(String c) { this.cnpj = c; }
    public void setTipoDocumento(String t) { this.tipoDocumento = t; }
    public void setEmail(String e) { this.email = e; }
    public void setTelefone(String t) { this.telefone = t; }
    public void setCep(String c) { this.cep = c; }
    public void setRua(String r) { this.rua = r; }
    public void setNumero(String n) { this.numero = n; }
    public void setBairro(String b) { this.bairro = b; }
    public void setCidade(String c) { this.cidade = c; }
    public void setUf(String u) { this.uf = u; }
    public void setPlanoTipo(PlanoTipo p) { this.planoTipo = p; }
    public void setStatusAssinatura(StatusAssinatura s) { this.statusAssinatura = s; }
    public void setTrialFim(LocalDate t) { this.trialFim = t; }
    public void setEmailRemetenteProprio(String e) { this.emailRemetenteProprio = e; }
    public void setEmailSenhaProprio(String s) { this.emailSenhaProprio = s; }
    public void setEmailNomeRemetente(String n) { this.emailNomeRemetente = n; }
}
