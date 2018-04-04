package client;

import java.io.IOException;
import java.net.InetAddress;

public class ClientMain {
	
	// The main entry for client
	public static void main(String[] args) throws IOException {
        int clientPort = Integer.parseInt(args[0]);
		InetAddress serverIp = InetAddress.getByName(args[1]);
		int serverPort = Integer.parseInt(args[2]);

        Client client = new Client(serverIp, serverPort, clientPort);
        try {
            client.start();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }
}
