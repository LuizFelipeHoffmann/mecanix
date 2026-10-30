package com.mecanix.service;

import com.mecanix.dto.EmailConfigRequest;
import com.mecanix.dto.EmailConfigResponse;
import com.mecanix.exception.BusinessException;
import com.mecanix.model.Empresa;
import com.mecanix.repository.EmpresaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// CRUD da conta de e-mail própria da oficina (ver Empresa.emailRemetenteProprio
// e EmailService). Restrito ao administrador — ver EmailConfigController.
@Service
public class EmailConfigService {
    private final EmpresaRepository empresaRepo;
    private final EmailService emailService;
    private final CryptoUtil crypto;

    public EmailConfigService(EmpresaRepository empresaRepo, EmailService emailService, CryptoUtil crypto) {
        this.empresaRepo = empresaRepo;
        this.emailService = emailService;
        this.crypto = crypto;
    }

    public EmailConfigResponse buscar(Long empresaId) {
        Empresa empresa = buscarEmpresa(empresaId);
        return EmailConfigResponse.from(empresa, emailService.getRemetente(empresa));
    }

    @Transactional
    public EmailConfigResponse atualizar(Long empresaId, EmailConfigRequest req) {
        Empresa empresa = buscarEmpresa(empresaId);

        if (req.isRemoverCredenciais()) {
            empresa.setEmailRemetenteProprio(null);
            empresa.setEmailSenhaProprio(null);
            empresa.setEmailNomeRemetente(null);
            empresaRepo.save(empresa);
            return EmailConfigResponse.from(empresa, emailService.getRemetente(empresa));
        }

        String novoEmail = req.getEmail() != null ? req.getEmail().trim() : null;
        if (novoEmail == null || novoEmail.isBlank())
            throw new BusinessException("Informe o e-mail da conta que vai enviar as mensagens");
        if (!novoEmail.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))
            throw new BusinessException("E-mail inválido");

        String novaSenha = req.getSenha() != null ? req.getSenha().trim() : null;
        boolean trocouEmail = empresa.getEmailRemetenteProprio() == null
            || !novoEmail.equalsIgnoreCase(empresa.getEmailRemetenteProprio());

        // Trocar o e-mail exige senha nova (a senha de app é vinculada à conta
        // antiga); mantendo o mesmo e-mail, dá pra só atualizar o nome exibido
        // sem reenviar a senha — ela não volta pro front depois de salva.
        if (trocouEmail && (novaSenha == null || novaSenha.isBlank()))
            throw new BusinessException("Informe a senha de app dessa conta de e-mail");

        empresa.setEmailRemetenteProprio(novoEmail);
        if (novaSenha != null && !novaSenha.isBlank()) {
            empresa.setEmailSenhaProprio(crypto.encrypt(novaSenha));
        }
        String nome = req.getNomeRemetente() != null ? req.getNomeRemetente().trim() : null;
        empresa.setEmailNomeRemetente(nome != null && !nome.isBlank() ? nome : null);

        empresaRepo.save(empresa);
        return EmailConfigResponse.from(empresa, emailService.getRemetente(empresa));
    }

    public void testar(Long empresaId, String destinatario) {
        Empresa empresa = buscarEmpresa(empresaId);
        if (empresa.getEmailRemetenteProprio() == null || empresa.getEmailSenhaProprio() == null)
            throw new BusinessException("Configure o e-mail e a senha antes de testar o envio");
        if (destinatario == null || destinatario.isBlank())
            throw new BusinessException("Informe um e-mail de destino pro teste");
        try {
            emailService.enviarTeste(empresa, destinatario.trim());
        } catch (Exception e) {
            throw new BusinessException("Não foi possível enviar — confira o e-mail e a senha de app. (" + e.getMessage() + ")");
        }
    }

    private Empresa buscarEmpresa(Long empresaId) {
        return empresaRepo.findById(empresaId)
            .orElseThrow(() -> new BusinessException("Empresa não encontrada"));
    }
}
