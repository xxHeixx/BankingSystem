package server;

import shared.Constant;
import shared.Reply;
import shared.Request;
import shared.SocketWrapper;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MonitoringTools {
    private static ArrayList<ClientMonitor> clientList = new ArrayList<>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void registerClient(ClientMonitor client, SocketWrapper serverSocket, Long duration) {
        clientList.add(client);
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                clientList.remove(client);
                byte[] stopMonitorReply = getStopMonitorReply();
                DatagramPacket packet = new DatagramPacket(stopMonitorReply, stopMonitorReply.length, client.getHost(),
                        client.getPort());
                serverSocket.sendPacket(packet);
                if (serverSocket.getErrMsg() != null) {
                    printError(serverSocket.getErrMsg(), client);
                }
            }
        },duration, TimeUnit.SECONDS);
        return ;
    }

    public static void updateClients(Request request, Reply reply, SocketWrapper serverSocket) {
        Reply monitorReply = constructMonitorReply (request, reply);
        byte[] data = Reply.marshal(monitorReply);
        for (ClientMonitor client : clientList) {
            DatagramPacket packet = new DatagramPacket(data, data.length, client.getHost(), client.getPort());
            serverSocket.sendPacket(packet);
            if (serverSocket.getErrMsg() != null) {
                printError(serverSocket.getErrMsg(), client);
            }
        }
    }

    private static Reply constructMonitorReply(Request request, Reply reply){
        List<String>result = new ArrayList<>();
        // Add request type to Payload
        result.add(request.getType());
        List<String>resultPayloads = reply.getPayLoads();
        List<String>requestPayloads = request.getPayLoads();
        switch (request.getType()) {
            // Add username, accountId, Balance, Currency
            case Request.SIGN_UP:
                result.add(requestPayloads.get(0));
                result.add(resultPayloads.get(1));
                result.add(requestPayloads.get(3));
                result.add(requestPayloads.get(2));
                break;
            // Add username, accountId
            case Request.CLOSE:
                result.add(requestPayloads.get(0));
                result.add(requestPayloads.get(1));
                break;
            // Add accountId, Amount deposit, currency deposit, New balance, currency balance
            case Request.DEPOSIT:
                result.add(requestPayloads.get(1));
                result.add(requestPayloads.get(4));
                result.add(requestPayloads.get(3));
                result.add(resultPayloads.get(1));
                result.add(resultPayloads.get(2));
                break;
            // Add accountId, Amount withdraw, currency withdraw, new balance, currency balance
            case Request.WITHDRAW:
                result.add(requestPayloads.get(1));
                result.add(requestPayloads.get(4));
                result.add(requestPayloads.get(3));
                result.add(resultPayloads.get(1));
                result.add(resultPayloads.get(2));
                break;
            // Add senderId, receiverId, Amount, Currency transferred
            case Request.TRANSFER:
                result.add(requestPayloads.get(1));
                result.add(requestPayloads.get(3));
                result.add(requestPayloads.get(5));
                result.add(requestPayloads.get(4));
                break;
            default:
                break;
        }
        return Reply.constructReply(false, result);
    }

    private static byte[] getStopMonitorReply() {
        ArrayList<String> payloads = new ArrayList<>();
        payloads.add(Request.MONITOR);
        payloads.add(Constant.STOP_MONITOR);
        Reply reply = Reply.constructReply(false, payloads);
        return Reply.marshal(reply);
    }

    private static void printError(String error, ClientMonitor client) {
        System.out.printf("Error %s on updating monitor data to client %s:%s\n", error, client.getHost(),
                client.getPort());
    }
}
