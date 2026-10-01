package com.mecanix.service;
import com.mecanix.dto.CadastroRequest;
import com.mecanix.dto.UsuarioResponse;
import com.mecanix.exception.BusinessException;
import com.mecanix.model.Empresa;
import com.mecanix.model.Usuario;
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
        return UsuarioResponse.from(u);
    }

    // Cria a Empresa (oficina que vai usar o MECANIX) junto com o primeiro
    // usuário dela (perfil ADMIN), numa única transação.
    @Transactional
    public UsuarioResponse cadastrarEmpresa(CadastroRequest req) {
        if (empresaRepo.existsByCnpj(req.getCnpj())) throw new BusinessException("CNPJ já cadastrado");
        if (repo.findByEmail(req.getEmailUsuario()).isPresent()) throw new BusinessException("E-mail já cadastrado");

        Empresa empresa = new Empresa();
        empresa.setNomeFantasia(req.getNomeFantasia());
        empresa.setRazaoSocial(req.getRazaoSocial());
        empresa.setCnpj(req.getCnpj());
        empresa.setTelefone(req.getTelefoneEmpresa());
        empresa.setEndereco(req.getEnderecoEmpresa());
        empresa.setEmail(req.getEmailUsuario());
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
}
