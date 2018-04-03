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

    public static String deleteUser(Integer accountId) {
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
    
    public static AccountInfo getUser(int accountNum){
    	return index.get(accountNum);
    }
    
    public static Double deposit(Integer accountId, Currency currency, Double amount) {
        addMoney(accountId, currency, amount);
        AccountInfo user = index.get(accountId);
        return user.getBalance();
    }
    
    public static int addMoney(int accountId, Currency newCurrency, Double amount){
    	AccountInfo user = index.get(accountId);
    	Double newAmount = amount*newCurrency.getRate()/user.getCurrency().getRate();
    	if (newAmount+user.getBalance()>=0){
    		user.setBalance(newAmount+user.getBalance());
    		index.put(accountId, user);
    	} else {
    		return -1;
    	}
    	return 0;
    }


}
