package server;

import shared.Currency;

import java.util.HashMap;
import java.util.Map;

public class BankingSystem {

    private static Map<Integer, AccountInfo> index = new HashMap<Integer, AccountInfo>();

    // Check user authentication, return null if correct info
    public static String checkUser(Integer accountNumber, String password, String userName) {
        ErrorCode errorCode = AuthTools.checkUser(accountNumber, password, userName);
        if (errorCode == null) {
            return null;
        }
        return errorCode.getMsg();
    }

    // Create new account, return account number
    public static Integer createUser(String userName, String password, Currency currency, Double balance) {
        AccountInfo userInfo = AccountInfo.createNewAccount(userName, currency, balance);
        Integer accountId = userInfo.getAccountId();
        index.put(accountId, userInfo);
        AuthTools.addUser(accountId, password, userName);
        return accountId;
    }

    // Delete account
    public static String deleteUser(Integer accountId) {
        String msg = null;
        try {
            index.remove(accountId);
            AuthTools.deleteUser(accountId);
        } catch (Exception e) {
            e.printStackTrace();
            msg = ErrorCode.ERROR_DELETE_ACCOUNT.getMsg();
        }
        return msg;

    }
    
    public static AccountInfo getUser(int accountNum){
    	return index.get(accountNum);
    }

    // Deposit money into account
    public static Double deposit(Integer accountId, Currency currency, Double amount) {
        addMoney(accountId, currency, amount);
        AccountInfo user = index.get(accountId);
        return user.getBalance();
    }

    // Withdraw money from account
    public static String withdraw(Integer accountId, Currency currency, Double amount) {
        Integer withdrawResult = addMoney(accountId, currency, amount * -1);
        if (withdrawResult < 0) {
            return ErrorCode.INSUFFICIENT_BALANCE.getMsg();
        }
        return null;
    }

    // Transfer money between accounts
    public static String transferMoney(int senderAccountId, int receiverAccountId, double amount, Currency currency) {
        if (!index.containsKey(receiverAccountId)) {
            return ErrorCode.INVALID_RECEIVER_ID.getMsg();
        }
        Integer minusBalanceResult = addMoney(senderAccountId, currency, amount * -1);
        if (minusBalanceResult < 0) {
            return ErrorCode.INSUFFICIENT_BALANCE.getMsg();
        }
        addMoney(receiverAccountId, currency, amount);
        return null;
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
