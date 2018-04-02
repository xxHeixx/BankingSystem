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

    public static Integer createUser(List<String> payLoads) {
        String userName = payLoads.get(0);
        String password = payLoads.get(1);
        Currency currency = Currency.valueFromString(payLoads.get(2));
        Double balance = Double.valueOf(payLoads.get(3));
        AccountInfo userInfo = AccountInfo.createNewAccount(userName, currency, balance);
        Integer accountId = userInfo.getAccountId();
        index.put(accountId, userInfo);
        AuthTools.addUser(accountId, password, userName);
        return accountId;
    }

    public static boolean deleteUser(List<String> payLoads) {
        String userName = payLoads.get(0);
        Integer accountId = Integer.valueOf(payLoads.get(1));
        String password = payLoads.get(2);
        return true;

    }

}
