package com.mecanix.config;

import com.mecanix.model.Empresa;
import com.mecanix.model.Usuario;
import com.mecanix.repository.EmpresaRepository;
import com.mecanix.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@Profile("dev")
public class DataInitializer {

    private static final String CNPJ_DEMO = "00.000.000/0001-00";

    @Bean
    public CommandLineRunner initUsuarios(UsuarioRepository repo, EmpresaRepository empresaRepo) {
        return args -> {
            BCryptPasswordEncoder enc = new BCryptPasswordEncoder();

            // O data.sql já cria a "Oficina Demo" (mesma empresa usada nos clientes/
            // veículos/estoque de exemplo) antes deste runner executar; se por algum
            // motivo ela ainda não existir, criamos aqui como fallback.
            Empresa empresa = empresaRepo.findByCnpj(CNPJ_DEMO).orElseGet(() -> {
                Empresa e = new Empresa();
                e.setNomeFantasia("Oficina Demo");
                e.setRazaoSocial("Oficina Demo LTDA");
                e.setCnpj(CNPJ_DEMO);
                e.setTelefone("(41) 3000-0000");
                e.setEndereco("Rua Demo, 1, Curitiba-PR");
                e.setEmail("admin@mecanix.com");
                return empresaRepo.save(e);
            });

            salvar(repo, enc, empresa, "admin@mecanix.com",    "admin123", "João Silva",  "Gerente",    Usuario.Perfil.ADMIN);
            salvar(repo, enc, empresa, "servicos@mecanix.com", "serv123",  "Paulo Ramos", "Atendente",  Usuario.Perfil.SERVICOS);
            salvar(repo, enc, empresa, "estoque@mecanix.com",  "est123",   "Carla Dias",  "Estoquista", Usuario.Perfil.ESTOQUE);
            System.out.println("╔══════════════════════════════════════════╗");
            System.out.println("║  MECANIX iniciado! (perfil: dev)         ║");
            System.out.println("║  Login: admin@mecanix.com / admin123     ║");
            System.out.println("╚══════════════════════════════════════════╝");
        };
    }

    private void salvar(UsuarioRepository repo, BCryptPasswordEncoder enc, Empresa empresa,
                        String email, String senha, String nome, String cargo, Usuario.Perfil perfil) {
        repo.findByEmail(email).ifPresentOrElse(
            u -> {},
            () -> {
                Usuario u = new Usuario();
                u.setNome(nome); u.setEmail(email); u.setSenha(enc.encode(senha));
                u.setCargo(cargo); u.setPerfil(perfil); u.setEmpresa(empresa);
                repo.save(u);
            }
        );
    }
}
