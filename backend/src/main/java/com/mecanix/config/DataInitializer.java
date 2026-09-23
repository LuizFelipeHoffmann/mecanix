package com.mecanix.config;

import com.mecanix.model.Usuario;
import com.mecanix.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@Profile("dev")
public class DataInitializer {

    @Bean
    public CommandLineRunner initUsuarios(UsuarioRepository repo) {
        return args -> {
            BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
            salvar(repo, enc, "admin@mecanix.com",    "admin123", "João Silva",  "Gerente",    Usuario.Perfil.ADMIN);
            salvar(repo, enc, "servicos@mecanix.com", "serv123",  "Paulo Ramos", "Atendente",  Usuario.Perfil.SERVICOS);
            salvar(repo, enc, "estoque@mecanix.com",  "est123",   "Carla Dias",  "Estoquista", Usuario.Perfil.ESTOQUE);
            System.out.println("╔══════════════════════════════════════════╗");
            System.out.println("║  MECANIX iniciado! (perfil: dev)         ║");
            System.out.println("║  Login: admin@mecanix.com / admin123     ║");
            System.out.println("╚══════════════════════════════════════════╝");
        };
    }

    private void salvar(UsuarioRepository repo, BCryptPasswordEncoder enc,
                        String email, String senha, String nome, String cargo, Usuario.Perfil perfil) {
        repo.findByEmail(email).ifPresentOrElse(
            u -> {},
            () -> {
                Usuario u = new Usuario();
                u.setNome(nome); u.setEmail(email); u.setSenha(enc.encode(senha));
                u.setCargo(cargo); u.setPerfil(perfil);
                repo.save(u);
            }
        );
    }
}
