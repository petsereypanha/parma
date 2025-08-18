package com.parma.user.jwt;

import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
@Component
public class JwtSecret {

    private static final int MIN_KEY_LENGTH = 32; // 256 bits
    private static final int MAX_KEY_LENGTH = 64; // 512 bits
    private static final String ALGORITHM = "HmacSHA256";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public SecretKey getSecretKey(String secret) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(secret);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            log.error("Error generating secret key: {}", e.getMessage());
            throw new RuntimeException("Failed to generate JWT secret key", e);
        }
    }
    public boolean isValidSecret(String secret) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(secret);
            return keyBytes.length >= MIN_KEY_LENGTH;
        } catch (Exception e) {
            log.error("Invalid JWT secret: {}", e.getMessage());
            return false;
        }
    }
    public static String generateNewSecret() {
        try {
            // Generate a random key length between MIN_KEY_LENGTH and MAX_KEY_LENGTH
            int keyLength = MIN_KEY_LENGTH + SECURE_RANDOM.nextInt(MAX_KEY_LENGTH - MIN_KEY_LENGTH + 1);
            byte[] keyBytes = new byte[keyLength];

            // Fill the array with cryptographically secure random bytes
            SECURE_RANDOM.nextBytes(keyBytes);

            // Additional entropy mixing
            for (int i = 0; i < keyBytes.length; i++) {
                keyBytes[i] ^= (byte) SECURE_RANDOM.nextInt(256);
            }

            // Verify the key meets minimum security requirements
            if (keyBytes.length < MIN_KEY_LENGTH) {
                throw new SecurityException("Generated key is too short");
            }

            // Encode the key in Base64
            String base64Key = Base64.getEncoder().encodeToString(keyBytes);

            // Log the key generation (without exposing the actual key)
            log.info("Generated new JWT secret key of length {} bits", keyBytes.length * 8);

            return base64Key;
        } catch (Exception e) {
            log.error("Failed to generate secure JWT secret: {}", e.getMessage());
            throw new SecurityException("Failed to generate secure JWT secret", e);
        }
    }
    public static boolean isValidSecretKey(String secretKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(secretKey);
            return keyBytes.length >= MIN_KEY_LENGTH && keyBytes.length <= MAX_KEY_LENGTH;
        } catch (Exception e) {
            log.error("Invalid secret key format: {}", e.getMessage());
            return false;
        }
    }
}
