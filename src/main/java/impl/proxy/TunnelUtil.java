package impl.proxy;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Running ssh -D command from java to set up socks5 over ssh
 * .............until we find a better solution.
 * jsch isn't what we're looking for.
 */
public class TunnelUtil {

    // To be able to kill a tunnel by destroying its process, this command isn't run in background "-f" ssh option
    private static final String startTunnelCommand = "ssh -N -i %s -D %s %s";
    private static final String bridgePortCommand = "socat tcp-listen:%s reuseaddr fork tcp:localhost:%s";
    private static final String killAllTunnelsCommand = "pkill ssh"; // this will interfere with the host machine...
    private static final String killAllBridgesCommand = "pkill socat";

    public static Process startTunnel(int port, String host, String sshkeyFile) throws IOException {
        return executeBashCommand(String.format(startTunnelCommand, sshkeyFile, port, host).split(" "));
    }

    public static Process bridgePort(int fromPort,  int toPort) throws IOException {
        //return executeBashCommand(String.format(bridgePortCommand, fromPort, toPort).split(" "));
        Runtime.getRuntime().exec(String.format(bridgePortCommand, fromPort, toPort));
        return null;

    }

    public static void killAllTunnels() throws IOException {
        executeBashCommand(killAllTunnelsCommand.split(" "));
    }

    public static void killAllBridges() throws IOException {
        executeBashCommand(killAllBridgesCommand.split(" "));
    }

    public static void refresh() throws IOException {
        killAllTunnels();
        killAllBridges();
    }

    private static Process executeBashCommand(String[] commands) throws IOException {
        System.out.println(Arrays.toString(commands));
        return new ProcessBuilder(commands)
                .inheritIO()
                .directory(new File(System.getProperty("user.home")))
                .start();
    }

    public static void main(String[] args) throws Exception {
        Process p = startTunnel(8080, "ubuntu@18.221.127.190", "keys/ohiokey.pem");
        Process p1 = bridgePort(7234, 9923);
        p.destroy();
    }
}
