package shared;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Request {

    public static final String SIGN_UP = "s";
    public static final String QUIT = "q";
    public static final String CLOSE = "c";
    public static final String WITHDRAW = "w";
    public static final String DEPOSIT = "d";
    public static final String BALANCE = "b";
    public static final String TRANSFER = "t";
    public static final String MONITOR = "m";
    private static int counter = 0;
    private String type;
    private String id;
    private List<String> payLoads = new ArrayList<>();

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public List<String> getPayLoads() {
        return payLoads;
    }

    public static Request createRequest(String type, List<String>payLoads) {
        counter++;
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String requestId = date + String.valueOf(counter);
        return new Request(String.valueOf(requestId), type, payLoads );
    }

    private Request(String id, String type, List<String>payLoads) {
        this.id = id;
        this.type = type;
        this.payLoads.addAll(payLoads);
    }

    public static byte[] marshal(Request request) {
        String requestType = request.getType();
        String requestId = request.getId();
        List<String> payLoads = request.getPayLoads();
        StringBuilder data = new StringBuilder();
        data.append(requestId).append(Constant.REQUEST_DELIM);
        data.append(requestType).append(Constant.REQUEST_DELIM);
        data.append(payLoads.size()).append(Constant.REQUEST_DELIM);
        for (String payload: payLoads) {
            data.append(payload).append(Constant.REQUEST_DELIM);
        }
        return data.toString().getBytes();
    }

    public static Request unmarshal(byte[] data) {
        String dataStr = new String(data);
        Scanner scanner = new Scanner(dataStr).useDelimiter(Constant.REQUEST_DELIM);
        String requestId = scanner.next();
        String requestType = scanner.next();
        int payloadSize = scanner.nextInt();
        List<String> payloads = new ArrayList<>();
        for (int i = 0; i < payloadSize; i++) {
            String s = scanner.next();
            payloads.add(s);
        }
        scanner.close();
        return new Request(requestId, requestType, payloads);
    }

}
