package com.mecanix.controller;
import com.mecanix.dto.LoginRequest;
import com.mecanix.dto.UsuarioResponse;
import com.mecanix.exception.BusinessException;
import com.mecanix.model.Usuario;
import com.mecanix.repository.UsuarioRepository;
import jakarta.servlet.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/auth")
public class AuthController {
    private final UsuarioRepository repo;
    private final BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
    public AuthController(UsuarioRepository repo){this.repo=repo;}

    @PostMapping("/login")
    public UsuarioResponse login(@RequestBody LoginRequest req, HttpServletRequest httpReq){
        Usuario u = repo.findByEmail(req.getEmail())
            .orElseThrow(() -> new BusinessException("E-mail ou senha incorretos"));
        if(!enc.matches(req.getSenha(), u.getSenha()))
            throw new BusinessException("E-mail ou senha incorretos");
        UsuarioResponse resp = UsuarioResponse.from(u);
        HttpSession session = httpReq.getSession(true);
        session.setAttribute("usuario", resp);
        session.setMaxInactiveInterval(28800); // 8 horas
        return resp;
    }

    @PostMapping("/logout")
    public Map<String,String> logout(HttpServletRequest req){
        HttpSession s = req.getSession(false);
        if(s != null) s.invalidate();
        return Map.of("msg","Logout realizado");
    }

    @GetMapping("/me")
    public Object me(HttpServletRequest req){
        HttpSession s = req.getSession(false);
        if(s == null) { throw new BusinessException("Não autenticado"); }
        return s.getAttribute("usuario");
    }
}
