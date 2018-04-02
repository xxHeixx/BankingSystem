package client;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.net.DatagramPacket;
import java.net.InetAddress;

import shared.Constant;
import shared.Reply;
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
    
    public void handleReply(Reply reply){
    	int replyStatus = reply.getStatusCode();
    	System.out.println(replyStatus);
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
                if (error.equals(socket.TIMEOUT)) {
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
    