package client;

public class ClientMain {
	public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        int mode = Integer.parseInt(args[1]);

        Client client = new Client(port, mode);
        try {
            client.start();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        while (true) {
            client.process();
        }
    }
}
