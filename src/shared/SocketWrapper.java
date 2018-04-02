package shared;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class SocketWrapper {

    private DatagramSocket socket;
    private int port;
    private String errMsg;

    public static final int MAX_PACKET_SIZE = 32768;

    public SocketWrapper(int port) throws SocketException {
        this.port = port;
        this.socket = new DatagramSocket(port);
    }

    public String getErrMsg() {
        return this.errMsg;
    }

    public DatagramPacket sendPacket() {
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

    public void sendPacket(DatagramPacket packet) {
        this.errMsg = null;
        try {
            socket.send(packet);
        } catch (IOException e) {
            errMsg = e.getMessage();
        }
    }
}
