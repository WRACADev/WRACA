<?php

/**
 * Class LoginEncryption
 *
 * This class provides methods for encrypting and decrypting data using AES in ECB mode and RSA algorithms.
 */
class LoginEncryption {

    // Private properties to store keys
    private $secretKey;
    private $privateKey;
    private $publicKey;

    /**
     * Constructor for the LoginEncryption class.
     *
     * @param string|null $secretKey   The secret key for AES encryption. If not provided, a random key will be generated.
     * @param string|null $privateKey  The private key for RSA encryption. If not provided, a key pair will be generated.
     * @param string|null $publicKey   The public key for RSA encryption. If not provided, a key pair will be generated.
     */
    public function __construct($secretKey = null, $privateKey = null, $publicKey = null) {
        $this->secretKey = $secretKey ?: self::generateAESKey();
        $keyPair = $privateKey && $publicKey ? ['privateKey' => $privateKey, 'publicKey' => $publicKey] : self::generateRSAKeyPair();
        $this->privateKey = $keyPair['privateKey'];
        $this->publicKey = $keyPair['publicKey'];
    }

    /**
     * Get the secret key used for AES encryption.
     *
     * @return string The secret key.
     */
    public function getSecretKey() {
        return $this->secretKey;
    }

    /**
     * Get the private key used for RSA encryption.
     *
     * @return string The private key.
     */
    public function getPrivateKey() {
        return $this->privateKey;
    }

    /**
     * Get the public key used for RSA encryption.
     *
     * @return string The public key.
     */
    public function getPublicKey() {
        return $this->publicKey;
    }

    /**
     * Encrypt the given plain text using AES in ECB mode and RSA.
     *
     * @param string $plainText The plain text to encrypt.
     * @return string The encrypted data.
     */
    public function encrypt($plainText) {
        $encryptedAES = self::encryptAES($plainText, $this->secretKey);
        openssl_public_encrypt($encryptedAES, $encryptedData, $this->publicKey);
        return $encryptedData;
    }

    /**
     * Decrypt the given encrypted data using RSA and AES in ECB mode.
     *
     * @param string $encryptedData The encrypted data to decrypt.
     * @return string The decrypted plain text.
     */
    public function decrypt($encryptedData) {
        openssl_private_decrypt($encryptedData, $decryptedAES, $this->privateKey);
        return self::decryptAES($decryptedAES, $this->secretKey);
    }

    /**
     * Generate a random AES key.
     *
     * @return string The generated AES key.
     */
    private static function generateAESKey() {
        $keyGen = openssl_cipher_iv_length("AES-256-ECB");
        return bin2hex(random_bytes($keyGen));
    }

    /**
     * Generate a key pair for RSA encryption.
     *
     * @return array An array containing the private and public keys.
     */
    private static function generateRSAKeyPair() {
        $config = [
            "digest_alg" => "sha512",
            "private_key_bits" => 2048,
            "private_key_type" => OPENSSL_KEYTYPE_RSA,
        ];
        $res = openssl_pkey_new($config);
        openssl_pkey_export($res, $privateKey);
        $publicKey = openssl_pkey_get_details($res)["key"];
        return ["privateKey" => $privateKey, "publicKey" => $publicKey];
    }

    /**
     * Encrypt the given plain text using AES in ECB mode.
     *
     * @param string $plainText The plain text to encrypt.
     * @param string $secretKey The secret key for encryption.
     * @return string The encrypted data.
     */
    private static function encryptAES($plainText, $secretKey) {
        $encryptedBytes = openssl_encrypt($plainText, "AES-256-ECB", $secretKey, 0);
        return base64_encode($encryptedBytes);
    }

    /**
     * Decrypt the given encrypted text using AES in ECB mode.
     *
     * @param string $encryptedText The encrypted text to decrypt.
     * @param string $secretKey The secret key for decryption.
     * @return string The decrypted plain text.
     */
    private static function decryptAES($encryptedText, $secretKey) {
        $encryptedBytes = base64_decode($encryptedText);
        $decryptedText = openssl_decrypt($encryptedBytes, "AES-256-ECB", $secretKey, 0);
        return $decryptedText;
    }
}

// DRIVER SCRIPT
$dudeEncryptor = new LoginEncryption();

// Sample data
$originalText = "Wow, this is some top-secret message, dude! 🤐";

// Encrypt the data
$encryptedData = $dudeEncryptor->encrypt($originalText);
echo "Encrypted message: $encryptedData\n";

// Decrypt the data
$decryptedText = $dudeEncryptor->decrypt($encryptedData);
echo "Decrypted message: $decryptedText\n";
