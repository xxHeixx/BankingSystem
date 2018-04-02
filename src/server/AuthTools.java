package server;

import javafx.util.Pair;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class AuthTools {
    public static final String INVALID_ACC = "Invalid account number";
    public static final String INVALID_PASS = "Invalid password";
    public static final String INVALID_NAME = "Invalid user name";
    public static final String CORRECT = "Correct";
    private static final String KEY = "some-secret-key-of-your-choice";
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

    private static String encrypt(final String text) {
        byte[] bytesEncoded = Base64.getEncoder().encode(text.getBytes());
        return new String(bytesEncoded);
    }

}
