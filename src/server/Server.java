package server;

import shared.*;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        DatagramPacket packet = socket.receivePacket();

        if (socket.getErrMsg() != null) {
            return socket.getErrMsg();
        }

        Request request = Request.unmarshal(packet.getData());
        String requestKey = packet.getAddress().getHostAddress() + '[' + request.getId() + ']';

        if (mode == InvocationSemantics.AT_MOST_ONCE && replyRecord.containsKey(requestKey)) {
            Reply reply = replyRecord.get(requestKey);
            error = sendReply(reply, packet.getAddress(), packet.getPort());
        } else {
            try {
                error = handleRequest(request, requestKey, packet.getAddress(), packet.getPort());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return error;
    }

    /**
     * Handle Request request with specific handle method respect to request type.
     * Return null if no error handling and processing request, else return error message.
     * @param request
     * @param requestKey
     * @return
     */
    private String handleRequest(Request request, String requestKey, InetAddress clientAddress, int clientPort) {
        Reply reply = null;
        switch (request.getType()) {
            case Request.SIGN_UP:
                reply =  processSignupRequest(request, requestKey);
                break;
            case Request.CLOSE:
                reply =  processCloseRequest(request, requestKey);
                break;
//            case Request.WITHDRAW:
//                return processEditBookingRequest(request, requestKey);
//            case Request.DEPOSIT:
//                return processMonitorRequest(request, requestKey);
//            case Request.BALANCE:
//                return processCancelBookingRequest(request, requestKey);
//            case Request.TRANSFER:
//                return processGetAllAvailableInTimeRangeRequest(request, requestKey);
            default:
                return "Invalid_operation";
        }
        String error = sendReply(reply, clientAddress, clientPort);
        List<String>payLoads = reply.getPayLoads();
        for(int i=0;i<payLoads.size();i++){
            System.out.println( "--" + payLoads.get(i));
        }
        if (error != null) {
            return error;
        }
        if (mode == InvocationSemantics.AT_MOST_ONCE) {
            replyRecord.put(requestKey, reply);
        }
        return null;
    }

    public Reply processSignupRequest(Request request, String requestKey) {
        List<String>result = new ArrayList<>();
        result.add(request.getType());
        List<String>payLoads = request.getPayLoads();
        String userName = payLoads.get(0);
        String password = payLoads.get(1);
        Currency currency = Currency.valueFromString(payLoads.get(2));
        Double balance = Double.valueOf(payLoads.get(3));
        Integer accountId = BankingSystem.createUser(userName, password, currency, balance);
        result.add(accountId.toString());
        Reply reply = Reply.constructReply(false, result);
        return reply;
    }

    public Reply processCloseRequest(Request request, String requestKey) {
        boolean hasError = false;
        List<String>result = new ArrayList<>();
        result.add(request.getType());
        List<String>payLoads = request.getPayLoads();
        String userName = payLoads.get(0);
        Integer accountNumber = Integer.valueOf(payLoads.get(1));
        String passWord = payLoads.get(2);
        // User authentication
        String authCheck = BankingSystem.checkUser(accountNumber, passWord, userName);
        if (authCheck != null) {
            hasError = true;
            result.add(authCheck);
        } else {
            // close account here
            String msg = BankingSystem.deleteUser(userName, passWord, accountNumber);
            result.add(msg);
        }
        Reply reply = Reply.constructReply(hasError, result);
        return reply;
    }

    public String sendReply(Reply reply, InetAddress clientHost, int clientPort) {
        byte[] data = Reply.marshal(reply);
        DatagramPacket packet = new DatagramPacket(data, data.length, clientHost, clientPort);
        if (socket.getErrMsg() != null) {
            return socket.getErrMsg();
        }
        socket.sendPacket(packet);
        return null;
    }
}
