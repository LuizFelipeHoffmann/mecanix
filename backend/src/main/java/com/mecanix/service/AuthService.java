package com.mecanix.service;
import com.mecanix.dto.CadastroRequest;
import com.mecanix.dto.PerfilRequest;
import com.mecanix.dto.SenhaRequest;
import com.mecanix.dto.UsuarioResponse;
import com.mecanix.exception.BusinessException;
import com.mecanix.model.Empresa;
import com.mecanix.model.PlanoTipo;
import com.mecanix.model.StatusAssinatura;
import com.mecanix.model.Usuario;
import java.time.LocalDate;
import com.mecanix.repository.EmpresaRepository;
import com.mecanix.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UsuarioRepository repo;
    private final EmpresaRepository empresaRepo;
    private final BCryptPasswordEncoder enc = new BCryptPasswordEncoder();

    public AuthService(UsuarioRepository repo, EmpresaRepository empresaRepo) {
        this.repo = repo;
        this.empresaRepo = empresaRepo;
    }

    public UsuarioResponse autenticar(String email, String senha) {
        Usuario u = repo.findByEmail(email)
            .orElseThrow(() -> new BusinessException("E-mail ou senha incorretos"));
        if (!enc.matches(senha, u.getSenha()))
            throw new BusinessException("E-mail ou senha incorretos");
        avisarSeTrialAcabou(u.getEmpresa());
        return UsuarioResponse.from(u);
    }

    // Se o período grátis (3 meses) já passou e ninguém confirmou pagamento
    // ainda, só marca a assinatura como PENDENTE — não existe cobrança
    // automática nem bloqueio de uso aqui; o front usa esse status (devolvido
    // em UsuarioResponse) pra avisar a pessoa que precisa confirmar o
    // pagamento. Roda a cada login porque o projeto não tem um job agendado.
    private void avisarSeTrialAcabou(Empresa empresa) {
        if (empresa == null || empresa.getStatusAssinatura() != StatusAssinatura.TRIAL) return;
        if (empresa.getTrialFim() != null && empresa.getTrialFim().isBefore(LocalDate.now())) {
            empresa.setStatusAssinatura(StatusAssinatura.PENDENTE);
            empresaRepo.save(empresa);
        }
    }

    // Cria a Empresa (oficina que vai usar o MECANIX) junto com o primeiro
    // usuário dela (perfil ADMIN), numa única transação.
    @Transactional
    public UsuarioResponse cadastrarEmpresa(CadastroRequest req) {
        String digitos = req.getCnpj() == null ? "" : req.getCnpj().replaceAll("\\D", "");
        boolean isCnpj = "CNPJ".equals(req.getTipoDocumento());
        if (isCnpj && digitos.length() != 14) throw new BusinessException("CNPJ inválido");
        if (!isCnpj && digitos.length() != 11) throw new BusinessException("CPF inválido");

        if (empresaRepo.existsByCnpj(req.getCnpj())) throw new BusinessException(isCnpj ? "CNPJ já cadastrado" : "CPF já cadastrado");
        if (repo.findByEmail(req.getEmailUsuario()).isPresent()) throw new BusinessException("E-mail já cadastrado");

        Empresa empresa = new Empresa();
        empresa.setNomeFantasia(req.getNomeFantasia());
        empresa.setRazaoSocial(req.getRazaoSocial());
        empresa.setCnpj(req.getCnpj());
        empresa.setTipoDocumento(req.getTipoDocumento());
        empresa.setTelefone(req.getTelefoneEmpresa());
        empresa.setCep(req.getCepEmpresa());
        empresa.setRua(req.getRuaEmpresa());
        empresa.setNumero(req.getNumeroEmpresa());
        empresa.setBairro(req.getBairroEmpresa());
        empresa.setCidade(req.getCidadeEmpresa());
        empresa.setUf(req.getUfEmpresa());
        empresa.setEmail(req.getEmailUsuario());

        PlanoTipo plano = PlanoTipo.GRATIS;
        try {
            if (req.getPlanoTipo() != null && !req.getPlanoTipo().isBlank())
                plano = PlanoTipo.valueOf(req.getPlanoTipo().toUpperCase());
        } catch (IllegalArgumentException ex) {
            // plano desconhecido vindo do front — mantém GRATIS em vez de travar o cadastro
        }
        empresa.setPlanoTipo(plano);
        empresa.setStatusAssinatura(StatusAssinatura.TRIAL);
        empresa.setTrialFim(LocalDate.now().plusDays(14));

        empresaRepo.save(empresa);

        Usuario u = new Usuario();
        u.setNome(req.getNomeUsuario());
        u.setEmail(req.getEmailUsuario());
        u.setSenha(enc.encode(req.getSenha()));
        u.setCargo("Administrador");
        u.setPerfil(Usuario.Perfil.ADMIN);
        u.setEmpresa(empresa);
        repo.save(u);

        return UsuarioResponse.from(u);
    }

    // Edita nome/foto do próprio usuário logado. Campos em branco/nulos no
    // request são ignorados (não apaga nome por engano); a foto só é removida
    // se vier removerFoto=true explicitamente.
    @Transactional
    public UsuarioResponse atualizarPerfil(Long usuarioId, PerfilRequest req) {
        Usuario u = repo.findById(usuarioId)
            .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        if (req.getNome() != null && !req.getNome().isBlank()) {
            u.setNome(req.getNome().trim());
        }
        if (req.isRemoverFoto()) {
            u.setFoto(null);
        } else if (req.getFoto() != null && !req.getFoto().isBlank()) {
            u.setFoto(req.getFoto());
        }

        repo.save(u);
        return UsuarioResponse.from(u);
    }

    // Troca a senha do próprio usuário logado, exigindo a senha atual (pra
    // alguém não poder trocar a senha só porque deixou a sessão aberta).
    @Transactional
    public void alterarSenha(Long usuarioId, SenhaRequest req) {
        Usuario u = repo.findById(usuarioId)
            .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        if (!enc.matches(req.getSenhaAtual(), u.getSenha()))
            throw new BusinessException("Senha atual incorreta");
        if (req.getSenhaNova() == null || req.getSenhaNova().length() < 6)
            throw new BusinessException("A nova senha deve ter no mínimo 6 caracteres");

        u.setSenha(enc.encode(req.getSenhaNova()));
        repo.save(u);
    }
}
