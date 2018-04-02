package shared;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Request {

    public static final String SIGN_UP = "s";
    public static final String CLOSE = "c";
    public static final String WITHDRAW = "w";
    public static final String DEPOSIT = "d";
    public static final String BALANCE = "b";
    public static final String TRANSFER = "t";
    private static int counter = 0;

    private String type;
    private String id;
    private String accountId;
    private List<String> payLoads = new ArrayList<>();

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getAccountId() {
        return accountId;
    }

    public List<String> getPayLoads() {
        return payLoads;
    }

    public static Request createRequest(String type, String accountId, List<String>payLoads) {
        return new Request(String.valueOf(counter++), type, accountId, payLoads );
    }

    private Request(String id, String type, String accountId, List<String>payLoads) {
        this.id = id;
        this.type = type;
        this.accountId = accountId;
        this.payLoads.addAll(payLoads);
    }

    public static byte[] marshal(Request request) {
        String requestType = request.getType();
        String requestId = request.getId();
        String requestUserId = request.getAccountId();
        List<String> payLoads = request.getPayLoads();
        StringBuilder data = new StringBuilder();
        data.append(requestId).append(Constant.REQUEST_DELIM);
        data.append(requestType).append(Constant.REQUEST_DELIM);
        data.append(requestUserId).append(Constant.REQUEST_DELIM);
        data.append(payLoads.size()).append(Constant.REQUEST_DELIM);
        for (String payload: payLoads) {
            data.append(payload).append(Constant.REQUEST_DELIM);
        }
        return data.toString().getBytes();
    }

    public static Request unmarshal(byte[] data) {
        String dataStr = new String(data);
        Scanner scanner = new Scanner(dataStr);
        String requestId = scanner.next();
        String requestType = scanner.next();
        String requestUserId = scanner.next();
        int payloadSize = scanner.nextInt();
        List<String> payloads = new ArrayList<>();
        for (int i = 0; i < payloadSize; i++) {
            String s = scanner.next();
            payloads.add(s);
        }
        scanner.close();
        return new Request(requestId, requestType, requestUserId, payloads);
    }

}
