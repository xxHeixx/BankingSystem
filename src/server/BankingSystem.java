package server;

import shared.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BankingSystem {

    private static Map<Integer, AccountInfo> index = new HashMap<Integer, AccountInfo>();

    public static String checkUser(Integer accountNumber, String password, String userName) {
        String authResult = AuthTools.checkUser(accountNumber, password, userName);
        if (authResult != AuthTools.CORRECT) {
            return authResult;
        } else {
            return null;
        }
    }

    public static Integer createUser(String userName, String password, Currency currency, Double balance) {

        AccountInfo userInfo = AccountInfo.createNewAccount(userName, currency, balance);
        Integer accountId = userInfo.getAccountId();
        index.put(accountId, userInfo);
        AuthTools.addUser(accountId, password, userName);
        return accountId;
    }

    public static String deleteUser(String userName, String password, Integer accountId) {
        String msg;
        try {
            index.remove(accountId);
            AuthTools.deleteUser(accountId);
            msg = "Account number " + accountId.toString() + " is deleted";
        } catch (Exception e) {
            e.printStackTrace();
            msg = "Error while trying to delete the account";
        }
        return msg;

    }
    
    public static Integer checkBalance(List<String> payLoads) {
    	Integer accountId = Integer.valueOf(payLoads.get(0));
        String password = payLoads.get(1);
        
        return accountId;
    }

}
