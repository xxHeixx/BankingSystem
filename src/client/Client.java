package client;

import java.net.SocketException;
import java.util.ArrayList;
import java.net.DatagramPacket;
import java.net.InetAddress;

import shared.Reply;
import shared.Request;
import shared.SocketWrapper;

public class Client {
	private static final String SIGN_UP_MSG = "You have successfully created a new account: %s\n";
	private static final String CLOSE_MSG = "Your account has been closed successfully\n";
	private static final String BALANCE_MSG = "Your account's balance: %.2f %s\n";
	private static final String DEPOSIT_MSG = "Your money has been deposited successfully. New balance: %.2f %s\n";
	private static final String WITHDRAW_MSG = "Your money has been withdrew successfully. New balance: %.2f %s\n";
	private static final String TRANSFER_MSG = "Your money has been transfered successfully. New balance: %.2f %s\n"; 
	
	private InetAddress serverIp;
	private int serverPort;
	private int clientPort;
    private SocketWrapper socket;

    public Client(InetAddress serverIp, int serverPort, int clientPort){
    	this.serverIp = serverIp;
        this.serverPort = serverPort;
		this.clientPort = clientPort;
    }
    public void start() throws SocketException {
    	System.out.println("Starting client...");
        socket = new SocketWrapper(clientPort);
    }
    public void exit() throws SocketException {
    	System.out.println("Exiting client...");
    	socket.close();
    }
    
    public void handleReply(Reply reply){
    	int replyStatus = reply.getStatusCode();
    	String replyErrMsg = reply.getErrMsg();
    	ArrayList<String> payloads = reply.getPayLoads();
		/*for(int i=0;i<payloads.size();i++){
			System.out.println(payloads.get(i));
		}*/
    	if (replyStatus == Reply.ERROR_REPLY_CODE){
    		System.out.printf("Error: %s\n", replyErrMsg);
    	} else{
    		String requestId = payloads.get(0);
    		Double balance;
    		switch(requestId){
    		case Request.SIGN_UP:
    			System.out.printf(SIGN_UP_MSG, payloads.get(1));
    			break;
    		case Request.CLOSE:
    			System.out.printf(CLOSE_MSG);
    			break;
    		case Request.BALANCE:
    			balance = Double.valueOf(payloads.get(1));
    			System.out.printf(BALANCE_MSG, balance, payloads.get(2));
    			break;
    		case Request.DEPOSIT:
    			balance = Double.valueOf(payloads.get(1));
    			System.out.printf(DEPOSIT_MSG, balance, payloads.get(2));
    			break;
    		case Request.WITHDRAW:
    			balance = Double.valueOf(payloads.get(1));
    			System.out.printf(WITHDRAW_MSG, balance, payloads.get(2));
    			break;
    		case Request.TRANSFER:
    			balance = Double.valueOf(payloads.get(1));
    			System.out.printf(TRANSFER_MSG, balance, payloads.get(2));
    			break;
    		default:
    		}
    	}
    }
    
    public String sendRequest(String requestId, ArrayList<String> payloads){
    	Request request = Request.createRequest(requestId, payloads.subList(1,payloads.size()));
    	byte[] data = Request.marshal(request);
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, serverIp, serverPort);
    	socket.sendPacket(sendPacket);
    	if (socket.getErrMsg() != null) {
             return socket.getErrMsg();
        }
    	while (true) {
            DatagramPacket replyPacket = socket.receivePacket();
            String error = socket.getErrMsg();
            if (error != null) {
                if (error.equals(SocketWrapper.TIMEOUT)) {
                    System.out.println("Server takes too long to reply. Resending request...");
                    socket.sendPacket(sendPacket);
                    continue;
                }
                return error;
            }
            Reply reply = Reply.unmarshal(replyPacket.getData());
            handleReply(reply);
            break;
        }
    	return null;
    }
}
    