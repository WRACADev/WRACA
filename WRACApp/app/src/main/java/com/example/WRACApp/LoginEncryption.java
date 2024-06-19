package com.example.wolseytechhr;

import android.os.Build;

import androidx.annotation.RequiresApi;

import javax.crypto.*;
import java.security.*;
import java.util.Base64;

/**
 * This class provides methods for generating AES and RSA keys, as well as encrypting and decrypting data using these keys.
 */
public class LoginEncryption {

    /**
     * Generates a SecretKey for AES encryption.
     *
     * @return The generated SecretKey.
     * @throws Exception If an error occurs during key generation.
     */
    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // You can also use 128 or 192 bits
        return keyGen.generateKey();
    }

    /**
     * Generates a KeyPair for RSA encryption.
     *
     * @return The generated KeyPair.
     * @throws NoSuchAlgorithmException If the RSA algorithm is not available.
     */
    public static KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }

    /**
     * Encrypts the given plain text using RSA public key and AES secret key.
     *
     * @param plainText  The text to be encrypted.
     * @param publicKey  The RSA public key.
     * @param secretKey  The AES secret key.
     * @return The encrypted data.
     * @throws Exception If an error occurs during encryption.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static byte[] encrypt(String plainText, PublicKey publicKey, SecretKey secretKey) throws Exception {
        plainText = encryptAES(plainText, secretKey);
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return rsaCipher.doFinal(plainText.getBytes());
    }

    /**
     * Decrypts the given encrypted data using RSA private key and AES secret key.
     *
     * @param encryptedData The data to be decrypted.
     * @param privateKey    The RSA private key.
     * @param secretKey     The AES secret key.
     * @return The decrypted text.
     * @throws Exception If an error occurs during decryption.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String decrypt(byte[] encryptedData, PrivateKey privateKey, SecretKey secretKey) throws Exception {
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedData = rsaCipher.doFinal(encryptedData);
        String firstLayerDecrypted = new String(decryptedData);
        return decryptAES(firstLayerDecrypted, secretKey);
    }

    /**
     * Encrypts the given plain text using AES secret key.
     *
     * @param plainText  The text to be encrypted.
     * @param secretKey  The AES secret key.
     * @return The encrypted text.
     * @throws NoSuchPaddingException    If the padding scheme is not available.
     * @throws NoSuchAlgorithmException  If the AES algorithm is not available.
     * @throws InvalidKeyException       If the secret key is invalid.
     * @throws BadPaddingException       If the padding is bad.
     * @throws IllegalBlockSizeException If the block size is illegal.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encryptAES(String plainText, SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * Decrypts the given encrypted text using AES secret key.
     *
     * @param encryptedText The text to be decrypted.
     * @param secretKey     The AES secret key.
     * @return The decrypted text.
     * @throws NoSuchPaddingException    If the padding scheme is not available.
     * @throws NoSuchAlgorithmException  If the AES algorithm is not available.
     * @throws InvalidKeyException       If the secret key is invalid.
     * @throws BadPaddingException       If the padding is bad.
     * @throws IllegalBlockSizeException If the block size is illegal.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String decryptAES(String encryptedText, SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }
}
