package server;

import shared.*;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("Duplicates")
public class Server {
    private int port;
    private InvocationSemantics mode;
    private SocketWrapper socket;
    private MaxSizeHashMap<String, Reply> replyRecord;
    private static final int lossFreq = 5;
    private static int counter = 0;

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
        // Simulate request loss
        counter ++;
        if (counter % lossFreq == 0) {
            return null;
        }

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
        LoggingTools.logRequest(request, clientAddress, clientPort);
        Reply reply = null;
        switch (request.getType()) {
            case Request.SIGN_UP:
                reply =  processSignupRequest(request);
                break;
            case Request.CLOSE:
                reply =  processCloseRequest(request);
                break;
            case Request.WITHDRAW:
                reply =  processWithdraw(request);
                break;
            case Request.DEPOSIT:
                reply = processDeposit(request);
                break;
            case Request.BALANCE:
                reply = processCheckBalance(request);
                break;
            case Request.TRANSFER:
                reply =  processTransferRequest(request);
                break;
            case Request.MONITOR:
                reply = processMonitorRequest(request, clientAddress, clientPort);
                break;
            default:
                return "Invalid_operation";
        }
        String error = sendReply(reply, clientAddress, clientPort);
        LoggingTools.logReply(reply);
        if(error == null && reply.getStatusCode() == Reply.SUCCESS_REPLY_CODE && request.getType()!= Request.MONITOR
                && request.getType()!= Request.BALANCE) {
            MonitoringTools.updateClients(request, reply, socket);
        }
        if(Constant.DEBUG) {
            List<String> payLoads = reply.getPayLoads();
            for (int i = 0; i < payLoads.size(); i++) {
                System.out.println("--" + payLoads.get(i));
            }
        }
        if (error != null) {
            System.out.println(error);
            return error;
        }
        if (mode == InvocationSemantics.AT_MOST_ONCE) {
            replyRecord.put(requestKey, reply);
        }
        return null;
    }

    public Reply processSignupRequest(Request request) {
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

    public Reply processCloseRequest(Request request) {
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
            Reply reply = Reply.constructReply(hasError, result);
            return reply;
        }
        // close account here
        String msg = BankingSystem.deleteUser(accountNumber);
        if (msg != null) {
            hasError = true;
        } else {
            msg = "Successfully close account number " + accountNumber.toString();
        }
        result.add(msg);
        Reply reply = Reply.constructReply(hasError, result);
        return reply;
    }
    
    public Reply processCheckBalance(Request request) {
        boolean hasError = false;
        List<String>payLoads = request.getPayLoads();
        String userName = payLoads.get(0);
        Integer accountNumber = Integer.valueOf(payLoads.get(1));
        String passWord = payLoads.get(2);
        
        List<String>result = new ArrayList<>();
        result.add(request.getType());
        // User authentication
        String authCheck = BankingSystem.checkUser(accountNumber, passWord, userName);
        if (authCheck != null) {
            hasError = true;
            result.add(authCheck);
            Reply reply = Reply.constructReply(hasError, result);
            return reply;
        }
    	Double balance = BankingSystem.getUser(accountNumber).getBalance();
    	result.add(Double.toString(balance));
    	result.add(BankingSystem.getUser(accountNumber).getCurrency().getAbbrv());
    	Reply reply = Reply.constructReply(hasError, result);
        return reply;
    } 
    
    public Reply processDeposit(Request request) {
        boolean hasError = false;
        List<String>payLoads = request.getPayLoads();
        String userName = payLoads.get(0);
        Integer accountNumber = Integer.valueOf(payLoads.get(1));
        String passWord = payLoads.get(2);
        Currency currency = Currency.valueFromString(payLoads.get(3));
        Double amount = Double.valueOf(payLoads.get(4));
        List<String>result = new ArrayList<>();
        result.add(request.getType());
        // User authentication
        String authCheck = BankingSystem.checkUser(accountNumber, passWord, userName);
        if (authCheck != null) {
            hasError = true;
            result.add(authCheck);
            Reply reply = Reply.constructReply(hasError, result);
            return reply;
        }
    	Double balance = BankingSystem.deposit(accountNumber, currency, amount);
    	result.add(Double.toString(balance));
    	result.add(BankingSystem.getUser(accountNumber).getCurrency().getAbbrv());
    	Reply reply = Reply.constructReply(hasError, result);
        return reply;
    }

    public Reply processWithdraw(Request request) {
        boolean hasError = false;
        List<String>result = new ArrayList<>();
        result.add(request.getType());
        // Extract payloads
        List<String>payLoads = request.getPayLoads();
        String userName = payLoads.get(0);
        Integer accountNumber = Integer.valueOf(payLoads.get(1));
        String passWord = payLoads.get(2);
        Currency currency = Currency.valueFromString(payLoads.get(3));
        Double amount = Double.valueOf(payLoads.get(4));

        // User authentication
        String authCheck = BankingSystem.checkUser(accountNumber, passWord, userName);
        if (authCheck != null) {
            hasError = true;
            result.add(authCheck);
            Reply reply = Reply.constructReply(hasError, result);
            return reply;
        }
        // Withdraw money
        String msg = BankingSystem.withdraw(accountNumber, currency, amount);
        if (msg != null) {
            hasError = true;
            result.add(msg);
        } else {
            Double balance = BankingSystem.getUser(accountNumber).getBalance();
            result.add(Double.toString(balance));
            result.add(BankingSystem.getUser(accountNumber).getCurrency().getAbbrv());
        }
        Reply reply = Reply.constructReply(hasError, result);
        return reply;
    }
   

    public Reply processTransferRequest(Request request) {
        boolean hasError = false;
        List<String>result = new ArrayList<>();
        result.add(request.getType());
        // Extract payloads
        List<String>payLoads = request.getPayLoads();
        String senderName = payLoads.get(0);
        Integer senderAccountId = Integer.valueOf(payLoads.get(1));
        String senderPassword = payLoads.get(2);
        Integer receiverAccountId = Integer.valueOf(payLoads.get(3));
        Currency currency = Currency.valueFromString(payLoads.get(4));
        Double amount = Double.valueOf(payLoads.get(5));

        // User authentication
        String authCheck = BankingSystem.checkUser(senderAccountId, senderPassword, senderName);
        if (authCheck != null) {
            hasError = true;
            result.add(authCheck);
            Reply reply = Reply.constructReply(hasError, result);
            return reply;
        }

        // Transfer money here
        String msg = BankingSystem.transferMoney(senderAccountId, receiverAccountId, amount, currency);
        if (msg != null) {
            hasError = true;
            result.add(msg);
        } else {
            Double balance = BankingSystem.getUser(senderAccountId).getBalance();
            result.add(Double.toString(balance));
            result.add(BankingSystem.getUser(senderAccountId).getCurrency().getAbbrv());
        }
        Reply reply = Reply.constructReply(hasError, result);
        return reply;
    }

    public Reply processMonitorRequest(Request request, InetAddress clientHost, int clientPort) {
        List<String> result = new ArrayList<>();
        result.add(Request.MONITOR);
        List<String> payLoads = request.getPayLoads();
        Long duration = Long.valueOf(payLoads.get(0));
        ClientMonitor client = new ClientMonitor(clientHost, clientPort);
        MonitoringTools.registerClient(client, socket, duration);
        result.add(Constant.START_MONITOR);
        Reply reply = Reply.constructReply(false, result);
        return reply;
    }

    public String sendReply(Reply reply, InetAddress clientHost, int clientPort) {
        // Simulate reply loss
        counter ++;
        if (counter % lossFreq == 0) {
            return null;
        }

        byte[] data = Reply.marshal(reply);
        DatagramPacket packet = new DatagramPacket(data, data.length, clientHost, clientPort);
        socket.sendPacket(packet);
        if (socket.getErrMsg() != null) {
            return socket.getErrMsg();
        }
        return null;
    }
}
