package client;

import java.net.SocketException;
import java.util.ArrayList;
import java.net.DatagramPacket;
import java.net.InetAddress;

import shared.Constant;
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
	
	private static final String MONITOR_SIGN_UP_MSG = "User %s have created new account %s with initial balance of %.2f %s\n";
	private static final String MONITOR_CLOSE_MSG = "User %s have closed account %s\n";
	private static final String MONITOR_DEPOSIT_MSG = "%.2f %s has been deposited into account %s. New balance: %.2f %s\n";
	private static final String MONITOR_WITHDRAW_MSG = "%.2f %s has been withdrew from account %s. New balance: %.2f %s\n";
	private static final String MONITOR_TRANSFER_MSG = "%.2f %s has been transferred from account %s to account %s\n";

	
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
    		case Request.MONITOR:
    			if (payloads.get(1).equals(Constant.START_MONITOR)){
    				System.out.println("Start Monitoring ...");
    				monitorLoop();
    				
    			}
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

    public String monitorLoop(){
    	while (true) {
            DatagramPacket replyPacket = socket.receivePacket();
            if(replyPacket == null){
            	continue;
            }
            String error = socket.getErrMsg();
            if (error != null && error!= SocketWrapper.TIMEOUT) {
            	System.out.printf("MonitorError: %s\n",error);
                return error;
            } 
           
            Reply reply = Reply.unmarshal(replyPacket.getData());
            error = handleMonitorReply(reply);
            if (error!=null && error.equals(Constant.STOP_MONITOR)){
            	break;
            } else if (error!=null){
            	System.out.printf("MonitorError: %s\n",error);
            	return error;
            }
        }
    	System.out.printf("End Monitoring\n");
    	return null;
    }
    
    public String handleMonitorReply(Reply reply){
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
    		Double balance, amount;
    		switch(requestId){
    		case Request.SIGN_UP:
    			amount = Double.valueOf(payloads.get(3));
    			System.out.printf(MONITOR_SIGN_UP_MSG, payloads.get(1),payloads.get(2),amount,payloads.get(4));
    			break;
    		case Request.CLOSE:
    			System.out.printf(MONITOR_CLOSE_MSG, payloads.get(1),payloads.get(2));
    			break;
    		case Request.DEPOSIT:
    			amount = Double.valueOf(payloads.get(2));
    			balance = Double.valueOf(payloads.get(4));
    			System.out.printf(MONITOR_DEPOSIT_MSG, amount, payloads.get(3), payloads.get(1),balance,payloads.get(5));
    			break;
    		case Request.WITHDRAW:
    			amount = Double.valueOf(payloads.get(2));
    			balance = Double.valueOf(payloads.get(4));
    			System.out.printf(MONITOR_WITHDRAW_MSG, amount, payloads.get(3), payloads.get(1),balance,payloads.get(5));
    			break;
    		case Request.TRANSFER:
    			amount = Double.valueOf(payloads.get(3));
    			System.out.printf(MONITOR_TRANSFER_MSG, amount, payloads.get(4),payloads.get(1),payloads.get(2));
    			break;
    		case Request.MONITOR:
    			if (payloads.size()>1){
    			return payloads.get(1);
    			} 
    			break;
    			
    		default:
    			return "No matching operation";
    		}
    	}
    	return null;
    }
}
   