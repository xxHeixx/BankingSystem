package server;

import shared.Currency;

public class AccountInfo {

    private static int counter = 10000;
    private int accountId;
    private String userName;
    private Currency currency;
    private double balance;
    private boolean inSession;

    public static  AccountInfo createNewAccount(String userName, Currency currency, double balance) {
        counter ++;
        return new AccountInfo(counter, userName, currency, balance);
    }

    private AccountInfo(Integer accountId, String userName, Currency currency, double balance) {
        this.accountId = accountId;
        this.userName = userName;
        this.currency = currency;
        this.balance = balance;
        this.inSession = true;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean isInSession() {
        return inSession;
    }

    public void setInSession(boolean inSession) {
        this.inSession = inSession;
    }

}
