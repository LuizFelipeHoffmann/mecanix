package com.mecanix.dto;
import com.mecanix.model.Usuario;

// Um acesso de funcionário (perfil SERVICOS ou ESTOQUE) dentro da equipe da
// empresa — visto só pelo administrador, na tela "Equipe" do perfil. Não
// inclui senha nem foto, só o necessário pra listar/gerenciar o acesso.
public class MembroEquipeResponse {
    private Long id;
    private String nome, email, cargo, perfil, iniciais;

    public static MembroEquipeResponse from(Usuario u) {
        MembroEquipeResponse r = new MembroEquipeResponse();
        r.id = u.getId();
        r.nome = u.getNome();
        r.email = u.getEmail();
        r.cargo = u.getCargo();
        r.perfil = u.getPerfil().name();
        if (u.getNome() != null) {
            String[] p = u.getNome().split(" ");
            r.iniciais = (p[0].substring(0, 1) + (p.length > 1 ? p[p.length - 1].substring(0, 1) : "")).toUpperCase();
        }
        return r;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getCargo() { return cargo; }
    public String getPerfil() { return perfil; }
    public String getIniciais() { return iniciais; }
}
