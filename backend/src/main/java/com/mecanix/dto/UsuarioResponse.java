package com.mecanix.dto;
import com.mecanix.model.Usuario;
public class UsuarioResponse {
    private Long id;
    private Long empresaId;
    private String nome,email,cargo,perfil,iniciais;
    private String planoTipo, statusAssinatura;
    private String trialFim; // yyyy-MM-dd, ou null se não houver teste em andamento
    private String empresaNome; // nome fantasia da oficina, cadastrado no "criar conta" — usado no cabeçalho da impressão da OS
    private String foto; // data-URI base64, ou null se a pessoa não cadastrou foto
    public static UsuarioResponse from(Usuario u){
        UsuarioResponse r=new UsuarioResponse();
        r.id=u.getId();r.empresaId=u.getEmpresa()!=null?u.getEmpresa().getId():null;
        r.nome=u.getNome();r.email=u.getEmail();r.cargo=u.getCargo();r.perfil=u.getPerfil().name();r.foto=u.getFoto();
        if(u.getNome()!=null){String[]p=u.getNome().split(" ");r.iniciais=(p[0].substring(0,1)+(p.length>1?p[p.length-1].substring(0,1):"")).toUpperCase();}
        if(u.getEmpresa()!=null){
            r.planoTipo = u.getEmpresa().getPlanoTipo()!=null ? u.getEmpresa().getPlanoTipo().name() : null;
            r.statusAssinatura = u.getEmpresa().getStatusAssinatura()!=null ? u.getEmpresa().getStatusAssinatura().name() : null;
            r.trialFim = u.getEmpresa().getTrialFim()!=null ? u.getEmpresa().getTrialFim().toString() : null;
            r.empresaNome = u.getEmpresa().getNomeFantasia();
        }
        return r;
    }
    public Long getId(){return id;} public Long getEmpresaId(){return empresaId;} public String getNome(){return nome;} public String getEmail(){return email;}
    public String getCargo(){return cargo;} public String getPerfil(){return perfil;} public String getIniciais(){return iniciais;}
    public String getPlanoTipo(){return planoTipo;} public String getStatusAssinatura(){return statusAssinatura;} public String getTrialFim(){return trialFim;}
    public String getEmpresaNome(){return empresaNome;}
    public String getFoto(){return foto;}
    // Setters usados só pra atualizar a cópia cacheada na sessão HTTP depois
    // de uma troca de plano ou edição de perfil, sem precisar deslogar/logar.
    public void setNome(String n){this.nome=n;} public void setFoto(String f){this.foto=f;}
    public void setPlanoTipo(String p){this.planoTipo=p;} public void setStatusAssinatura(String s){this.statusAssinatura=s;}
    public void setTrialFim(String t){this.trialFim=t;}
}
