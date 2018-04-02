package shared;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class SocketWrapper {

    private DatagramSocket socket;
    private int port;
    private String errMsg;

    public static final String TIMEOUT = "timeout";
    public static final int timeout = 10000;
    public static final int MAX_PACKET_SIZE = 32768;

    public SocketWrapper(int port) throws SocketException {
        this.port = port;
        this.socket = new DatagramSocket(port);
        socket.setSoTimeout(timeout);
    }

    public String getErrMsg() {
        return this.errMsg;
    }

    public DatagramPacket receivePacket() {
        this.errMsg = null;
        byte[] data = new byte[MAX_PACKET_SIZE];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            if (e instanceof SocketTimeoutException) {
                errMsg = TIMEOUT;
            } else {
                errMsg = e.getMessage();
            }
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

    public String getIp() {
        return this.socket.getLocalAddress().getHostAddress();
    }

    public void close() {
        if (socket != null) {
            socket.close();
        }
    }
}
