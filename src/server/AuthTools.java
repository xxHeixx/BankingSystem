package server;

import javafx.util.Pair;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


public class AuthTools {
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

    public static ErrorCode checkUser (Integer accountNumber, String password, String userName){
        try {
            String encryptedPassword = encrypt(password);
            if (!passwordMap.containsKey(accountNumber)) {
                return ErrorCode.INVALID_ACC;
            }
            Pair<String, String>entryInfo = passwordMap.get(accountNumber);
            if (!encryptedPassword.equals(entryInfo.getKey())) {
                return ErrorCode.INVALID_PASS;
            } else if (!userName.equals(entryInfo.getValue())){
                return ErrorCode.INVALID_NAME;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorCode.INVALID_ACC;
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
