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
		+ "Account sign up: s,<name>,<password>,<currency>,<initial_balance>\n"
		+ "Close account: c,<name>,<account_number>,<password>\n"
		+ "Check balance: b,<name>,<account_number>,<password>\n"
		+ "Deposit money: d,<name>,<account_number>,<password>,<currency>,<amount>\n"
		+ "Withdraw money: w,<name>,<account_number>,<password>,<currency>,<amount>\n"
		+ "Transfer money: t,<name>,<account_number>,<password>,<received_account_number>,<currency>,<amount>\n"
		+ "Monitor accounts: m,<period_in_seconds>\n"
		+ "Quit client: q\n";
	private static final String HELPCODE = "h";
	private static final String ERRTEXT_INPUT = "Input Error: %s\n";
	private static final String ERRTEXT_SERVER = "Server Error: %s\n";
	
	public static void main(String[] args) throws IOException {
        int clientPort = Integer.parseInt(args[0]);
		InetAddress serverIp = InetAddress.getByName(args[1]);
		int serverPort = Integer.parseInt(args[2]);

        Client client = new Client(serverIp, serverPort, clientPort);
        try {
            client.start();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        String[] params;
        String error;
        boolean clientOpen = true;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (clientOpen) {
            System.out.print(INSTRUCTION);
            params = br.readLine().split(",");
            if (params[0].equals(HELPCODE)) {
                System.out.println(HELPTEXT);
                continue;
            } else {
            	error = InputChecking.checkSyntax(params);
            	if (error!=InputChecking.CORRECT){
            		System.out.printf(ERRTEXT_INPUT, error);
            		continue;
            	}
            	switch (params[0]) {
                case Request.SIGN_UP:
                case Request.CLOSE:
                case Request.BALANCE:
                case Request.DEPOSIT:
                case Request.WITHDRAW:
                case Request.TRANSFER:
                case Request.MONITOR:
                	ArrayList<String> data = new ArrayList<>(Arrays.asList(params));
                	error = client.sendRequest(params[0],data);
                    if (error != null) {
                        System.out.printf(ERRTEXT_SERVER, error);
                    }
                    break;
                case Request.QUIT:
                	clientOpen = false;
                	client.exit();
                	break;
                default:
                    System.out.printf(ERRTEXT_INPUT, "Code should not run to this part!");
                    continue;
                }
                
            }
        }

    }
}
