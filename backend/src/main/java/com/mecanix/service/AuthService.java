package com.mecanix.service;
import com.mecanix.dto.UsuarioResponse;
import com.mecanix.exception.BusinessException;
import com.mecanix.model.Usuario;
import com.mecanix.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UsuarioRepository repo;
    private final BCryptPasswordEncoder enc = new BCryptPasswordEncoder();

    public AuthService(UsuarioRepository repo) {
        this.repo = repo;
    }

    public UsuarioResponse autenticar(String email, String senha) {
        Usuario u = repo.findByEmail(email)
            .orElseThrow(() -> new BusinessException("E-mail ou senha incorretos"));
        if (!enc.matches(senha, u.getSenha()))
            throw new BusinessException("E-mail ou senha incorretos");
        return UsuarioResponse.from(u);
    }
}
