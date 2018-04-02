package client;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.net.DatagramPacket;
import java.net.InetAddress;

import shared.Constant;
import shared.Request;
import shared.SocketWrapper;

public class Client {
	private InetAddress serverIp;
	private int serverPort;
    private SocketWrapper socket;

    public Client(InetAddress serverIp, int serverPort){
    	this.serverIp = serverIp;
        this.serverPort = serverPort;
    }
    public void start() throws SocketException {
        socket = new SocketWrapper(serverPort);
    }
    
    public void handleReply(Request reply){
    	String replyType = reply.getType();
    	System.out.println(replyType);
    }
    
    public String op_SignUp(String name, String password, String currencyType){
    	ArrayList<String> payloads = new ArrayList<>();
    	payloads.add(name);
    	payloads.add(password);
    	payloads.add(currencyType);
    	Request request = Request.createRequest(type, accountId, payLoads)
    	return null;
    }
    
    public String run(){
        while (true) {
            DatagramPacket packet = socket.receivePacket();
            String error = socket.getErrMsg();
            if (error != null) {
                if (error.equals(socket.TIMEOUT)) {
                    System.out.println("Timeout receiving reply. Retransmit request...");
                    //socket.sendRequest(request);
                    continue;
                }
                return error;
            }
            Request reply = Request.unmarshal(packet.getData());
            handleReply(reply);
        }
    }
}
