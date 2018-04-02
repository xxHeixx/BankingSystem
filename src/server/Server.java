package server;

import shared.MaxSizeHashMap;
import shared.Reply;
import shared.Request;
import shared.SocketWrapper;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private int port;
    private InvocationSemantics mode;
    private SocketWrapper socket;
    private MaxSizeHashMap<String, Reply> replyRecord;

    private enum InvocationSemantics {
        AT_MOST_ONCE (0), AT_LEAST_ONCE (1);

        private final int id;
        private static Map<Integer, InvocationSemantics> map = new HashMap<Integer, InvocationSemantics>();

        static {
            for (InvocationSemantics semanticEnum : InvocationSemantics.values()) {
                map.put(semanticEnum.id, semanticEnum);
            }
        }

        InvocationSemantics (int id) {this.id = id;}

        public static InvocationSemantics valueOf (int id) {
            return map.get(id);
        }
    }

    public Server(int port, int mode){
        this.port = port;
        this.mode = InvocationSemantics.valueOf(mode);
        replyRecord = new MaxSizeHashMap<String, Reply>(100);
    }

    public void start() throws SocketException{
        this.socket = new SocketWrapper(port);
        System.out.printf("Server listening on port: %s\n", port);

    }

    /**
     * Receive request via current socket that bound to.
     * Unmarshall byte array data from socket receivePacket method to a request.
     * Do filtering if at most once invocation semantic is chosen, handle request with handleRequest.
     * Return null if no error receiving and handling request, else return error message.
     * @return
     */
    public String processRequest() {
        String error = null;
        Reply reply = null;
        DatagramPacket packet = socket.receivePacket();

        if (socket.getErrMsg() != null) {
            return socket.getErrMsg();
        }

        Request request = Request.unmarshal(packet.getData());
        String requestKey = packet.getAddress().getHostAddress() + '[' + request.getId() + ']';

        if (mode == InvocationSemantics.AT_MOST_ONCE && replyRecord.containsKey(requestKey)) {
            reply = replyRecord.get(requestKey);
        } else {
            try {
                reply = handleRequest(request, requestKey);
                if (mode == InvocationSemantics.AT_MOST_ONCE) {
                    replyRecord.put(requestKey, reply);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        error = sendReply(reply, packet.getAddress(), packet.getPort());
        return error;
    }


    public String sendReply(Reply reply, InetAddress clientHost, int clientPort) {
        byte[] data = Reply.marshal(reply);
        DatagramPacket packet = new DatagramPacket(data, data.length, clientHost, clientPort);
        if (socket.getErrMsg() != null) {
            return socket.getErrMsg();
        }
        return null;
    }
}
