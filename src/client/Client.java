package client;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;

import client.ClientString;
import shared.Constant;
import shared.Reply;
import shared.Request;
import shared.SocketWrapper;

public class Client {
	private InetAddress serverIp;
	private int serverPort;
	private int clientPort;
    private SocketWrapper socket;

    public Client(InetAddress serverIp, int serverPort, int clientPort){
    	this.serverIp = serverIp;
        this.serverPort = serverPort;
		this.clientPort = clientPort;
    }
    
    // Send a marshaled request to the server and wait for reply
    private String sendRequest(String requestId, ArrayList<String> payloads){
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
                    System.out.printf(ClientString.RESENDING_MSG);
                    socket.sendPacket(sendPacket);
                    continue;
                }
                return error;
            }
            Reply reply = Reply.unmarshal(replyPacket.getData());
            int replyStatus = reply.getStatusCode();
        	error = reply.getErrMsg();
        	if (replyStatus == Reply.ERROR_REPLY_CODE){
        		return error;
        	} else{
        		handleReply(reply);
        	}
            break;
        }
    	return null;
    }

    // Depend on the operation, display the reply accordingly
    private void handleReply(Reply reply){   	
    	ArrayList<String> payloads = reply.getPayLoads();
    	if(Constant.DEBUG) {
			for (int i = 0; i < payloads.size(); i++) {
				System.out.printf("--%s\n",payloads.get(i));
			}
		}
		String requestId = payloads.get(0);
		Double balance;
		switch(requestId){
		case Request.SIGN_UP:
			System.out.printf(ClientString.SIGN_UP_MSG, payloads.get(1));
			break;
		case Request.CLOSE:
			System.out.printf(ClientString.CLOSE_MSG);
			break;
		case Request.BALANCE:
			balance = Double.valueOf(payloads.get(1));
			System.out.printf(ClientString.BALANCE_MSG, balance, payloads.get(2));
			break;
		case Request.DEPOSIT:
			balance = Double.valueOf(payloads.get(1));
			System.out.printf(ClientString.DEPOSIT_MSG, balance, payloads.get(2));
			break;
		case Request.WITHDRAW:
			balance = Double.valueOf(payloads.get(1));
			System.out.printf(ClientString.WITHDRAW_MSG, balance, payloads.get(2));
			break;
		case Request.TRANSFER:
			balance = Double.valueOf(payloads.get(1));
			System.out.printf(ClientString.TRANSFER_MSG, balance, payloads.get(2));
			break;
		case Request.MONITOR:
			if (payloads.size()>1){
    			if (payloads.get(1).equals(Constant.START_MONITOR)){
    				System.out.printf(ClientString.START_MONITOR_MSG);
    				monitorLoop();		
    			}
			}
			break;
		default:
		}
    }
    
    // Enter monitoring mode, keep waiting for update from server
    private String monitorLoop(){
    	while (true) {
            DatagramPacket replyPacket = socket.receivePacket();
            if(replyPacket == null){
            	continue;
            }
            String error = socket.getErrMsg();
            if (error != null && error!= SocketWrapper.TIMEOUT) {
            	System.out.printf(ClientString.ERR_MSG,error);
                return error;
            } 
           
            Reply reply = Reply.unmarshal(replyPacket.getData());
            int replyStatus = reply.getStatusCode();
        	error = reply.getErrMsg();
        	if (replyStatus == Reply.ERROR_REPLY_CODE){
        		System.out.printf(ClientString.ERR_MSG, error);
        	} else {
	            error = handleMonitorReply(reply);
	            if (error.equals(Constant.STOP_MONITOR)){
	            	break;
	            } else if (error!=null){
	            	System.out.printf(ClientString.ERR_MSG,error);
	            	return error;
	            }
        	}
        }
    	System.out.printf(ClientString.END_MONITOR_MSG);
    	return null;
    }
    
    // Handle update from server during monitoring mode
    private String handleMonitorReply(Reply reply){
    	ArrayList<String> payloads = reply.getPayLoads();	
		String requestId = payloads.get(0);
		Double balance, amount;
		switch(requestId){
		case Request.SIGN_UP:
			amount = Double.valueOf(payloads.get(3));
			System.out.printf(ClientString.MONITOR_SIGN_UP_MSG, payloads.get(1),payloads.get(2),amount,payloads.get(4));
			break;
		case Request.CLOSE:
			System.out.printf(ClientString.MONITOR_CLOSE_MSG, payloads.get(1),payloads.get(2));
			break;
		case Request.DEPOSIT:
			amount = Double.valueOf(payloads.get(2));
			balance = Double.valueOf(payloads.get(4));
			System.out.printf(ClientString.MONITOR_DEPOSIT_MSG, amount, payloads.get(3), payloads.get(1),balance,payloads.get(5));
			break;
		case Request.WITHDRAW:
			amount = Double.valueOf(payloads.get(2));
			balance = Double.valueOf(payloads.get(4));
			System.out.printf(ClientString.MONITOR_WITHDRAW_MSG, amount, payloads.get(3), payloads.get(1),balance,payloads.get(5));
			break;
		case Request.TRANSFER:
			amount = Double.valueOf(payloads.get(3));
			System.out.printf(ClientString.MONITOR_TRANSFER_MSG, amount, payloads.get(4),payloads.get(1),payloads.get(2));
			break;
		case Request.MONITOR:
			if (payloads.size()>1){
				return payloads.get(1);
			} 
			break;
		default:
			return ClientString.NOMATCH_MONITOR_MSG;
		}
    	return null;
    }
    
    // Start the client and take input from user
    public void start() throws IOException {
    	System.out.printf(ClientString.START_MSG);
        socket = new SocketWrapper(clientPort);
        String[] params;
        String error;
        boolean clientOpen = true;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (clientOpen) {
            System.out.print(ClientString.INSTRUCTION_MSG);
            params = br.readLine().split(Constant.REQUEST_DELIM);
            if (params[0].equals(ClientString.HELPCODE)) {
                System.out.println(ClientString.HELP_MSG);
                continue;
            } else {
            	error = InputChecking.checkSyntax(params);
            	if (error!=ClientString.CORRECT){
            		System.out.printf(ClientString.ERR_INPUT_MSG, error);
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
                	error = sendRequest(params[0],data);
                    if (error != null) {
                        System.out.printf(ClientString.ERR_MSG, error);
                    }
                    break;
                case Request.QUIT:
                	clientOpen = false;
                	exit();
                	break;
                default:
                    System.out.printf(ClientString.ERR_INPUT_MSG, "Code should not run to this part!");
                    continue;
                }
                
            }
        }

    }
    
    // Quit the client
    public void exit() throws SocketException {
    	System.out.printf(ClientString.EXIT_MSG);
    	socket.close();
    }

}
   