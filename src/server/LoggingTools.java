package server;

import shared.Constant;
import shared.Reply;
import shared.Request;

import java.net.InetAddress;
import java.util.List;

public class LoggingTools {
    private static final String SIGN_UP_REQ = "Request: User %s creates account with password %s, initial balance %s %s\n";
    private static final String CLOSE_REQ = "Request: User %s close account number %s with password %s\n";
    private static final String BALANCE_REQ = "Request: User %s check balance account number %s with password %s\n";
    private static final String DEPOSIT_REQ = "Request: User %s deposit %s %s into account number %s with password %s \n";
    private static final String WITHDRAW_REQ = "Request: User %s withdraw %s %s from account number %s with password %s \n";
    private static final String TRANSFER_REQ = "Request: User %s transfer %s %s to account number %s from account number %s with password %s \n";
    private static final String MONITOR_REQ = "Request: Client at host %s and port %s request monitor for %s seconds \n";

    private static final String ERROR_REP = "Invalid request: %s \n";
    private static final String SIGN_UP_REP = "Reply: Account number %s is created\n";
    private static final String CLOSE_REP = "Reply: %s\n";
    private static final String BALANCE_REP = "Reply: Balance is %s\n";
    private static final String DEPOSIT_REP = "Reply: Deposit successfully. New balance: %.2f\n";
    private static final String WITHDRAW_REP = "Reply: Withdraw successfully. New balance: %.2f \n";
    private static final String TRANSFER_REP = "Reply: Transfer successfully. New balance: %.2f \n";
    private static final String MONITOR_REP = "Reply: " + Constant.START_MONITOR;

    public static void logRequest(Request request, InetAddress clientHost, int clientPort) {
        List<String> payLoads = request.getPayLoads();
        switch(request.getType()){
            case Request.SIGN_UP:
                System.out.printf(SIGN_UP_REQ, payLoads.get(0), payLoads.get(1), payLoads.get(3), payLoads.get(2));
                break;
            case Request.CLOSE:
                System.out.printf(CLOSE_REQ, payLoads.get(0), payLoads.get(1), payLoads.get(2));
                break;
            case Request.BALANCE:
                System.out.printf(BALANCE_REQ, payLoads.get(0), payLoads.get(1), payLoads.get(2));
                break;
            case Request.DEPOSIT:
                System.out.printf(DEPOSIT_REQ, payLoads.get(0), payLoads.get(4), payLoads.get(3), payLoads.get(1), payLoads.get(2));
                break;
            case Request.WITHDRAW:
                System.out.printf(WITHDRAW_REQ, payLoads.get(0), payLoads.get(4), payLoads.get(3), payLoads.get(1), payLoads.get(2));
                break;
            case Request.TRANSFER:
                System.out.printf(TRANSFER_REQ, payLoads.get(0), payLoads.get(5), payLoads.get(4), payLoads.get(3), payLoads.get(1), payLoads.get(2));
                break;
            case Request.MONITOR:
                System.out.printf(MONITOR_REQ, clientHost.toString(), String.valueOf(clientPort), payLoads.get(0));
        }
    }

    public static void logReply(Reply reply) {
        if (reply.getStatusCode() == Reply.ERROR_REPLY_CODE) {
            System.out.printf(ERROR_REP, reply.getErrMsg());
            return;
        }
        List<String> payLoads = reply.getPayLoads();
        Double balance;
        switch (payLoads.get(0)){
            case Request.SIGN_UP:
                System.out.printf(SIGN_UP_REP, payLoads.get(1));
                break;
            case Request.CLOSE:
                System.out.printf(CLOSE_REP, payLoads.get(1));
                break;
            case Request.BALANCE:
                balance = Double.valueOf(payLoads.get(1));
                System.out.printf(BALANCE_REP, balance, payLoads.get(2));
                break;
            case Request.DEPOSIT:
                balance = Double.valueOf(payLoads.get(1));
                System.out.printf(DEPOSIT_REP, balance, payLoads.get(2));
                break;
            case Request.WITHDRAW:
                balance = Double.valueOf(payLoads.get(1));
                System.out.printf(WITHDRAW_REP, balance, payLoads.get(2));
                break;
            case Request.TRANSFER:
                balance = Double.valueOf(payLoads.get(1));
                System.out.printf(TRANSFER_REP, balance, payLoads.get(2));
                break;
            case Request.MONITOR:
                System.out.println(MONITOR_REP);
        }
    }
}
