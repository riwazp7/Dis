package proxy;

import java.io.IOException;

public class TunnelManager {

    private static final String startTunnelCommand = "ssh -Nf -i keys/ohiokey.pem -D %s %s";

    public static void startTunnel(int port, String host) throws IOException {
        executeBashCommand(String.format(startTunnelCommand, port, host));
    }

    private static void executeBashCommand(String command) throws IOException {
        Runtime.getRuntime().exec(command);
    }

    public static void main(String[] args) throws Exception {
        startTunnel(8080, "ubuntu@18.221.115.184");
    }

}
