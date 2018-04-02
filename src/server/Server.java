package server;

import shared.SocketWrapper;

import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private int port;
    private InvocationSemantics mode;
    private SocketWrapper socket;

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
    }

    public void start() throws SocketException{
        this.socket = new SocketWrapper(port);
        System.out.printf("Server listening on port: %s\n", port);

    }

    public void process() {

    }
}
