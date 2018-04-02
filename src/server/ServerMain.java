package server;

public class ServerMain {
    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        int mode = Integer.parseInt(args[1]);

        Server server = new Server(port, mode);
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        while (true) {
            server.processRequest();
        }
    }
}
