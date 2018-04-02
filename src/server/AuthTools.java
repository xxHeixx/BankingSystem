package server;

import javafx.util.Pair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AuthTools {
    public static final String INVALID_ACC = "Invalid account number";
    public static final String INVALID_PASS = "Invalid password";
    public static final String INVALID_NAME = "Invalid user name";
    public static final String CORRECT = "Correct";
    private static SecretKeySpec key;
    private static Map<Integer, Pair<String, String>> passwordMap = new HashMap<>();

    public static void addUser ( Integer accountNumber, String password, String userName){
        try {
            String encryptedPassword = encrypt(password);
            Pair<String, String> entryInfo = new Pair<>(encryptedPassword, userName);
            passwordMap.put(accountNumber, entryInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String checkUser (Integer accountNumber, String password, String userName){
        try {
            String encryptedPassword = encrypt(password);
            if (!passwordMap.containsKey(accountNumber)) {
                return INVALID_ACC;
            }
            Pair<String, String>entryInfo = passwordMap.get(accountNumber);
            if (!encryptedPassword.equals(entryInfo.getKey())) {
                return INVALID_PASS;
            } else if (!userName.equals(entryInfo.getValue())){
                return INVALID_NAME;
            } else {
                return CORRECT;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return INVALID_ACC;
        }
    }

    public static void deleteUser (Integer accountNumber){
        try {
            passwordMap.remove(accountNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initialize() throws NoSuchAlgorithmException, InvalidKeySpecException{
        String password = "1AS12s32dw";
        byte[] salt = new String("12345678").getBytes();
        int iterationCount = 40000;
        // Other values give me java.security.InvalidKeyException: Illegal key size or default parameters
        int keyLength = 128;
        key = createSecretKey(password.toCharArray(),
                salt, iterationCount, keyLength);
    }

    private static SecretKeySpec createSecretKey(char[] password, byte[] salt, int iterationCount, int keyLength) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        PBEKeySpec keySpec = new PBEKeySpec(password, salt, iterationCount, keyLength);
        SecretKey keyTmp = keyFactory.generateSecret(keySpec);
        return new SecretKeySpec(keyTmp.getEncoded(), "AES");
    }

    public static String encrypt(String property) throws GeneralSecurityException, UnsupportedEncodingException {
        Cipher pbeCipher = Cipher.getInstance("AES");
        pbeCipher.init(Cipher.ENCRYPT_MODE, key);
        AlgorithmParameters parameters = pbeCipher.getParameters();
        IvParameterSpec ivParameterSpec = parameters.getParameterSpec(IvParameterSpec.class);
        byte[] cryptoText = pbeCipher.doFinal(property.getBytes("UTF-8"));
        byte[] iv = ivParameterSpec.getIV();
        return base64Encode(iv) + ":" + base64Encode(cryptoText);
    }

    private static String base64Encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }
}
