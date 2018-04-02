package client;

import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.net.DatagramPacket;
import client.ReplyReceiver.Reply_Mode;

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
    	String id = reply.getType();
    }
    
    public String run(){
        while (true) {
            DatagramPacket packet = socket.receivePacket();
            String error = socket.getErrMsg();
            if (error != null) {
                /*if (error.equals(ClientSocket.TIMEOUT)) {
                    System.out.println("Timeout receiving reply. Retransmit request...");
                    clientSocket.sendRequest(request);
                    continue;
                }*/
                return error;
            }
            Request reply = Request.unmarshal(packet.getData());
            if (reply.statusCode == Reply.ERROR_REPLY_CODE) {
                return reply.getErrorMessage();

            } else {
                callback.handle(reply.getPayloads());
                if (replyReceiveMode == Reply_Mode.NORMAL) {
                    break;
                } else {
                    while (true) {
                        data = clientSocket.receiveReply();
                        error = clientSocket.error();
                        if (error != null) {
                            if (!error.equals(ClientSocket.TIMEOUT)) {
                                return error;
                            }
                            continue;
                        }
                        reply = Reply.unmarshal(data);
                        callback.handle(reply.getPayloads());
                        if (reply.getPayloads().get(1).equals(Constant.STOP_MONITOR)) {
                            break;
                        }
                    }
                }
            }
        }
        return null;
    }
}
