package com.example.osteoporosis_detection.util;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import android.util.Base64;

public class EncryptionUtil {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";
    private static final int KEY_LENGTH = 16; // 16 bytes for 128-bit key

    public static String encrypt(String value, String secretKey) throws Exception {
        Key key = generateKey(secretKey);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedValue = cipher.doFinal(value.getBytes());
        return Base64.encodeToString(encryptedValue, Base64.DEFAULT);
    }

    public static String decrypt(String value, String secretKey) throws Exception {
        Key key = generateKey(secretKey);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedValue = cipher.doFinal(Base64.decode(value, Base64.DEFAULT));
        return new String(decryptedValue);
    }

    private static Key generateKey(String secretKey) throws Exception {
        byte[] keyBytes = new byte[KEY_LENGTH];
        byte[] secretKeyBytes = secretKey.getBytes();
        System.arraycopy(secretKeyBytes, 0, keyBytes, 0, Math.min(secretKeyBytes.length, KEY_LENGTH));
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }
}
