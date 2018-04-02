package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import shared.Constant;
import shared.Request;

public class SocketWrapper {

	private DatagramSocket socket;
	private InetAddress serverHost;
    private int serverPort;
    private String errMsg;

    public static final int MAX_PACKET_SIZE = 32768;

    public SocketWrapper(InetAddress serverHost, int serverPort) throws SocketException {
        this.serverPort = serverPort;
        this.serverHost = serverHost;
        this.socket = new DatagramSocket(serverPort);
    }
    
    public void sendRequest(Request request) {
    	this.errMsg = null;
        byte[] data = Request.marshal(request);
        DatagramPacket packet = new DatagramPacket(data, data.length);
        try {
            socket.send(packet);
        } catch (IOException e) {
            errMsg = e.getMessage();
        }
    }

    public DatagramPacket receivePacket() {
        this.errMsg = null;
        byte[] data = new byte[MAX_PACKET_SIZE];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            errMsg = e.getMessage();
            return null;
        }
        return packet;
    }
}
