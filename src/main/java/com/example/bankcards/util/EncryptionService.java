package com.example.bankcards.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class EncryptionService {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_SIZE = 12;

    @Value("${encryption.secret}")
    private String secret;

    public String encrypt(String data) {
        try {
            SecretKey key = getKey();
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            byte[] nonce = new byte[GCM_SIZE];
            SecureRandom random = new SecureRandom();
            random.nextBytes(nonce);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, nonce);

            cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);
            byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

            byte[] combined = new byte[GCM_SIZE + encryptedData.length];
            System.arraycopy(nonce, 0, combined, 0, GCM_SIZE);
            System.arraycopy(encryptedData, 0, combined, GCM_SIZE, encryptedData.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception ex) {
            throw new RuntimeException("Encryption failed", ex);
        }
    }

    public String decrypt(String encryptedData) {
        try {
            SecretKey key = getKey();
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            byte[] combined = Base64.getDecoder().decode(encryptedData);
            byte[] nonce = new byte[GCM_SIZE];
            byte[] encryptedBytes = new byte[combined.length - GCM_SIZE];

            System.arraycopy(combined, 0, nonce, 0, GCM_SIZE);
            System.arraycopy(combined, GCM_SIZE, encryptedBytes, 0, encryptedBytes.length);

            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, nonce);
            cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);

            byte[] decryptedData = cipher.doFinal(encryptedBytes);
            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new RuntimeException("Decryption failed", ex);
        }
    }

    private SecretKey getKey() {
        return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), ALGORITHM);
    }
}
