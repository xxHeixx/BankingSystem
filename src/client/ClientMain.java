package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;

import shared.Request;

public class ClientMain {
	private static final String INSTRUCTION = "Input your operation (input h for help):\n";
	private static final String HELPTEXT 
		= "List of available operations (parameters must be seperated by ','):\n\n"
		+ "Sign up new account: s,<name>,<password>,<currency>,<initial_balance>\n"
		+ "Close account: c,<name>,<account_number>,<password>\n"
		+ "Check account's balance: b,<account_number>,<password>\n"
		+ "Deposit money: d,<account_number>,<password>,<amount>\n"
		+ "Withdraw money: w,<account_number>,<password>,<amount>\n"
		+ "Transfer money: t,<account_number>,<password>,<received_account_number>,<amount>\n";
	private static final String HELPCODE = "h";
	
	public static void main(String[] args) throws IOException {
		InetAddress serverIp = InetAddress.getByName(args[0]);
		int serverPort = Integer.parseInt(args[1]);

        Client client = new Client(serverIp, serverPort);
        try {
            client.start();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        String[] params;
        String error;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print(INSTRUCTION);
            params = br.readLine().split(",");
            if (params[0].equals(HELPCODE)) {
                System.out.println(HELPTEXT);
                continue;
            } else {
                switch (params[0]) {
                case Request.SIGN_UP:
                case Request.CLOSE:
                case Request.BALANCE:
                case Request.DEPOSIT:
                case Request.TRANSFER:
                case Request.WITHDRAW:
                	error = client.sendRequest(params[0], (ArrayList<String>) Arrays.asList(params));
                    if (error != null) {
                        System.out.printf("Error with operation %s: %s\n",params[0], error);
                    }
                    break;
                default:
                    System.out.println("Invalid operation, please input again (input h for help)\n");
                }
            }
        }

    }
}
