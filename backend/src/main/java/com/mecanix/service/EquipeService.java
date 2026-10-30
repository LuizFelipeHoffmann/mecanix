package com.mecanix.service;

import com.mecanix.dto.AtualizarMembroRequest;
import com.mecanix.dto.CriarMembroRequest;
import com.mecanix.dto.MembroEquipeResponse;
import com.mecanix.exception.BusinessException;
import com.mecanix.model.Empresa;
import com.mecanix.model.Usuario;
import com.mecanix.repository.EmpresaRepository;
import com.mecanix.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Gerencia os acessos de funcionário (perfil SERVICOS/ESTOQUE) dentro da
// empresa do administrador logado. Nunca cria/edita um ADMIN por aqui —
// quem precisa de acesso total já tem a própria conta criada no cadastro.
@Service
public class EquipeService {
    private final UsuarioRepository repo;
    private final EmpresaRepository empresaRepo;
    private final BCryptPasswordEncoder enc = new BCryptPasswordEncoder();

    public EquipeService(UsuarioRepository repo, EmpresaRepository empresaRepo) {
        this.repo = repo;
        this.empresaRepo = empresaRepo;
    }

    public List<MembroEquipeResponse> listar(Long empresaId) {
        return repo.findByEmpresaId(empresaId).stream()
            .filter(u -> u.getPerfil() != Usuario.Perfil.ADMIN) // a conta do próprio admin é editada na aba "Perfil", não aqui
            .map(MembroEquipeResponse::from)
            .toList();
    }

    @Transactional
    public MembroEquipeResponse criar(Long empresaId, CriarMembroRequest req) {
        validarPerfilRestrito(req.getPerfil());
        String email = req.getEmail().trim();
        if (repo.findByEmail(email).isPresent())
            throw new BusinessException("E-mail já cadastrado");

        Empresa empresa = empresaRepo.findById(empresaId)
            .orElseThrow(() -> new BusinessException("Empresa não encontrada"));

        Usuario u = new Usuario();
        u.setNome(req.getNome().trim());
        u.setEmail(email);
        u.setSenha(enc.encode(req.getSenha()));
        u.setCargo(req.getCargo());
        u.setPerfil(Usuario.Perfil.valueOf(req.getPerfil().toUpperCase()));
        u.setEmpresa(empresa);
        repo.save(u);
        return MembroEquipeResponse.from(u);
    }

    @Transactional
    public MembroEquipeResponse atualizar(Long empresaId, Long usuarioId, AtualizarMembroRequest req) {
        Usuario u = buscarDaEquipe(empresaId, usuarioId);

        if (req.getNome() != null && !req.getNome().isBlank()) u.setNome(req.getNome().trim());
        if (req.getCargo() != null) u.setCargo(req.getCargo());
        if (req.getPerfil() != null && !req.getPerfil().isBlank()) {
            validarPerfilRestrito(req.getPerfil());
            u.setPerfil(Usuario.Perfil.valueOf(req.getPerfil().toUpperCase()));
        }
        if (req.getSenha() != null && !req.getSenha().isBlank()) {
            if (req.getSenha().length() < 6) throw new BusinessException("A senha deve ter no mínimo 6 caracteres");
            u.setSenha(enc.encode(req.getSenha()));
        }

        repo.save(u);
        return MembroEquipeResponse.from(u);
    }

    @Transactional
    public void remover(Long empresaId, Long usuarioId) {
        Usuario u = buscarDaEquipe(empresaId, usuarioId);
        repo.delete(u);
    }

    // Garante isolamento entre empresas (multi-tenant) e que só acessos
    // SERVICOS/ESTOQUE sejam editados/removidos por aqui — nunca o próprio
    // admin nem o usuário de outra oficina.
    private Usuario buscarDaEquipe(Long empresaId, Long usuarioId) {
        Usuario u = repo.findById(usuarioId)
            .orElseThrow(() -> new BusinessException("Acesso não encontrado"));
        if (u.getEmpresa() == null || !u.getEmpresa().getId().equals(empresaId))
            throw new BusinessException("Acesso não encontrado");
        if (u.getPerfil() == Usuario.Perfil.ADMIN)
            throw new BusinessException("Não é possível gerenciar um administrador por aqui");
        return u;
    }

    private void validarPerfilRestrito(String perfil) {
        if (perfil == null || !(perfil.equals("SERVICOS") || perfil.equals("ESTOQUE")))
            throw new BusinessException("Nível de acesso inválido — escolha Atendimento ou Estoque");
    }
}
