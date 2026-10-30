package com.mecanix.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

// Criptografa/descriptografa dados sensíveis guardados no banco — hoje, só a
// senha de app da conta de e-mail própria de cada oficina (ver Empresa e
// EmailConfigService). AES-256-GCM com uma chave derivada de um segredo que
// fica só na configuração do backend (CRYPTO_SECRET), nunca no banco.
//
// Diferente da senha de LOGIN do usuário (um hash BCrypt, de mão única —
// nunca precisa ser lida de volta), isso PRECISA ser reversível: o backend
// tem que conseguir recuperar a senha original pra autenticar no servidor
// SMTP (Gmail) da oficina na hora de enviar um e-mail.
@Component
public class CryptoUtil {
    private static final String ALGORITMO = "AES/GCM/NoPadding";
    private static final int TAMANHO_IV = 12;    // bytes — recomendado pro GCM
    private static final int TAMANHO_TAG_BITS = 128;

    private final SecretKeySpec chave;

    public CryptoUtil(@Value("${mecanix.crypto.secret}") String segredo) {
        try {
            // SHA-256 do segredo configurado vira a chave AES-256 — assim o
            // segredo pode ter qualquer tamanho/formato nas variáveis de ambiente.
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(segredo.getBytes(StandardCharsets.UTF_8));
            this.chave = new SecretKeySpec(hash, "AES");
        } catch (Exception e) {
            throw new IllegalStateException("Não foi possível inicializar a criptografia (CRYPTO_SECRET ausente?)", e);
        }
    }

    // Retorna base64(IV + texto cifrado) — o IV vai junto porque ele não é
    // segredo, só precisa ser diferente a cada vez (e é, via SecureRandom).
    public String encrypt(String textoPuro) {
        try {
            byte[] iv = new byte[TAMANHO_IV];
            new SecureRandom().nextBytes(iv);
            Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(Cipher.ENCRYPT_MODE, chave, new GCMParameterSpec(TAMANHO_TAG_BITS, iv));
            byte[] cifrado = cipher.doFinal(textoPuro.getBytes(StandardCharsets.UTF_8));

            byte[] saida = new byte[iv.length + cifrado.length];
            System.arraycopy(iv, 0, saida, 0, iv.length);
            System.arraycopy(cifrado, 0, saida, iv.length, cifrado.length);
            return Base64.getEncoder().encodeToString(saida);
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao criptografar dado sensível", e);
        }
    }

    public String decrypt(String base64) {
        try {
            byte[] entrada = Base64.getDecoder().decode(base64);
            byte[] iv = new byte[TAMANHO_IV];
            System.arraycopy(entrada, 0, iv, 0, TAMANHO_IV);
            byte[] cifrado = new byte[entrada.length - TAMANHO_IV];
            System.arraycopy(entrada, TAMANHO_IV, cifrado, 0, cifrado.length);

            Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(Cipher.DECRYPT_MODE, chave, new GCMParameterSpec(TAMANHO_TAG_BITS, iv));
            return new String(cipher.doFinal(cifrado), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao descriptografar dado sensível", e);
        }
    }
}
