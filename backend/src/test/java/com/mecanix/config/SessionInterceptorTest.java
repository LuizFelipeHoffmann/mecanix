package com.mecanix.config;

import com.mecanix.dto.UsuarioResponse;
import com.mecanix.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

class SessionInterceptorTest {

    private final SessionInterceptor interceptor = new SessionInterceptor();

    private MockHttpServletRequest req(String metodo, String uri, Usuario.Perfil perfil) {
        MockHttpServletRequest r = new MockHttpServletRequest(metodo, uri);
        if (perfil != null) {
            Usuario u = new Usuario();
            u.setNome("Carla Dias");
            u.setEmail("estoque@mecanix.com");
            u.setPerfil(perfil);
            r.getSession().setAttribute("usuario", UsuarioResponse.from(u));
        }
        return r;
    }

    private int status(String metodo, String uri, Usuario.Perfil perfil) throws Exception {
        MockHttpServletResponse res = new MockHttpServletResponse();
        boolean ok = interceptor.preHandle(req(metodo, uri, perfil), res, null);
        assertEquals(ok, res.getStatus() == 200, "retorno e status devem concordar");
        return res.getStatus();
    }

    @Test
    void semSessaoRetorna401() throws Exception {
        assertEquals(401, status("GET", "/api/clientes", null));
        assertEquals(401, status("POST", "/api/ordens", null));
    }

    @Test
    void preflightPassaSemSessao() throws Exception {
        assertEquals(200, status("OPTIONS", "/api/clientes", null));
    }

    @Test
    void leituraLiberadaParaQualquerPerfil() throws Exception {
        // o Dashboard carrega OS e alertas de estoque nos tres perfis
        assertEquals(200, status("GET", "/api/ordens", Usuario.Perfil.ESTOQUE));
        assertEquals(200, status("GET", "/api/estoque/alertas", Usuario.Perfil.SERVICOS));
    }

    @Test
    void escritaForaDoModuloRetorna403() throws Exception {
        assertEquals(403, status("DELETE", "/api/clientes/1", Usuario.Perfil.ESTOQUE));
        assertEquals(403, status("POST", "/api/ordens", Usuario.Perfil.ESTOQUE));
        assertEquals(403, status("PUT", "/api/estoque/1", Usuario.Perfil.SERVICOS));
    }

    @Test
    void escritaNoProprioModuloPassa() throws Exception {
        assertEquals(200, status("POST", "/api/estoque", Usuario.Perfil.ESTOQUE));
        assertEquals(200, status("PUT", "/api/clientes/1", Usuario.Perfil.SERVICOS));
        assertEquals(200, status("DELETE", "/api/ordens/1", Usuario.Perfil.ADMIN));
        assertEquals(200, status("POST", "/api/ordens/1/enviar-email", Usuario.Perfil.SERVICOS));
    }
}
