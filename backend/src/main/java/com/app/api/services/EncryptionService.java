package com.app.api.services;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Service responsible for encrypting and decrypting sensitive application data
 * using AES-256 in GCM mode.
 * <p>
 * Encrypted values are stored as Base64 strings. A random IV is generated for
 * every encryption operation and prepended to the ciphertext before encoding.
 * </p>
 */
@Service
public class EncryptionService {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;

    private final SecretKey secretKey;

    /**
     * Encryption service contructor
     * Initialised the secretKey
     */
    public EncryptionService(
        @Value("${app.encryption.key}") String base64Key
    ){
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);
        System.out.println(decodedKey.length);
        this.secretKey = new SecretKeySpec(decodedKey, "AES");
    }

    /**
     * Encrypts plain text using AES-256-GCM.
     *
     * @param plainText value to encrypt
     * @return Base64 encoded encrypted value
     */
    public String encrypt(String plainText){
        if(plainText == null || plainText.isBlank()){
            return plainText;
        }

        try{
            byte[] iv = new byte[IV_LENGTH];
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(
                Cipher.ENCRYPT_MODE, 
                secretKey, 
                new GCMParameterSpec(TAG_LENGTH, iv)
            );

            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            ByteBuffer buffer = ByteBuffer.allocate(iv.length + cipherText.length);

            buffer.put(iv);
            buffer.put(cipherText);
            return Base64.getEncoder().encodeToString(buffer.array());
        }catch(Exception e){
            throw new RuntimeException("Failed to encrypt data,", e);
        }
    }

        /**
        * Decrypts an AES-256-GCM encrypted value.
        *
        * @param encryptedText Base64 encoded ciphertext
        * @return original plaintext
        */    
    public String decrypt(String encryptedText){
        if(encryptedText == null || encryptedText.isBlank()){
            return encryptedText;
        }

        try{
            byte[] decoded = Base64.getDecoder().decode(encryptedText);

            ByteBuffer buffer = ByteBuffer.wrap(decoded);
            byte[] iv = new byte[IV_LENGTH];
            buffer.get(iv);

            byte[] cipherText = new byte[buffer.remaining()];
            buffer.get(cipherText);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(
                Cipher.DECRYPT_MODE, 
                secretKey, 
                new GCMParameterSpec(TAG_LENGTH, iv)
            );

            byte[] plain = cipher.doFinal(cipherText);
            return new String(plain, StandardCharsets.UTF_8);
        }catch(Exception e){
            throw new RuntimeException("Failed to decrypt data.", e);
        }
    }

    /**
     * Generates random AES-256 key
     */

    public static String generateKey(){
        try{
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(256);

            return Base64.getEncoder().encodeToString(
                generator.generateKey().getEncoded()
            );
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
