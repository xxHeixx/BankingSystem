package server;

import java.net.InetAddress;

public class ClientMonitor {
    private InetAddress host;
    private int port;

    public InetAddress getHost() {
        return host;
    }

    public void setHost(InetAddress host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ClientMonitor(InetAddress host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof ClientMonitor) {
            ClientMonitor other = (ClientMonitor) object;
            if (this.host == other.getHost() && this.port == other.getPort()) {
                return true;
            }
        }
        return false;
    }
}
