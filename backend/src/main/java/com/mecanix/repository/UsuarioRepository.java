package com.mecanix.repository;
import com.mecanix.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario,Long> {
    Optional<Usuario> findByEmail(String email);
    // Lista os usuários (acessos) de uma empresa — usado na tela "Equipe" do
    // perfil, pra administrador gerenciar os acessos de atendimento/estoque.
    List<Usuario> findByEmpresaId(Long empresaId);
}
