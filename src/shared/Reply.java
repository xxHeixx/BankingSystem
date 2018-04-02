package shared;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Reply {
    public final static int ERROR_REPLY_CODE = 1;
    public final static int SUCCESS_REPLY_CODE = 0;

    private ArrayList<String> payLoads = new ArrayList<>();
    private String errMsg;
    private int statusCode;

    public ArrayList<String> getPayLoads() {
        return payLoads;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public int getStatusCode() {
        return statusCode;
    }

    private Reply(int statusCode, List<String> payLoads) {
        this.statusCode = statusCode;
        if (statusCode == ERROR_REPLY_CODE) {
            this.errMsg = payLoads.get(0);
        }
        this.payLoads.addAll(payLoads);
    }

    public static Reply constructReply(boolean hasError, List<String> payLoads) {
        int statusCode = hasError ? ERROR_REPLY_CODE : SUCCESS_REPLY_CODE;
        return new Reply(statusCode, payLoads);
    }

    public static Reply unmarshal(byte[] data) {
        String dataStr = new String(data);
        Scanner scanner = new Scanner(dataStr);
        int statusCode = scanner.nextInt();
        int payloadSize = scanner.nextInt();
        List<String> payloads = new ArrayList<>();
        for (int i = 0; i < payloadSize; i++) {
            String s = scanner.next();
            payloads.add(s);
        }
        scanner.close();
        return new Reply(statusCode, payloads);
    }

    public static byte[] marshal(Reply reply) {
        int statusCode = reply.getStatusCode();
        List<String> replyData = reply.getPayLoads();
        StringBuilder data = new StringBuilder();
        data.append(statusCode).append(Constant.REQUEST_DELIM);
        data.append(replyData.size()).append(Constant.REQUEST_DELIM);
        for (String s : replyData) {
            data.append(s).append(Constant.REQUEST_DELIM);
        }
        return data.toString().getBytes();
    }
}
