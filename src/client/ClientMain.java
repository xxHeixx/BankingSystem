package client;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ClientMain {
	public static void main(String[] args) throws UnknownHostException {
		InetAddress serverIp = InetAddress.getByName(args[0]);
		int serverPort = Integer.parseInt(args[1]);

        Client client = new Client(serverIp, serverPort);
        try {
            client.start();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        client.run();
    }
}
