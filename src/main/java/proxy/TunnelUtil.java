package proxy;

import java.io.File;
import java.io.IOException;

/**
 * Running ssh -D command from java to set up socks5 over ssh
 * .............until we find a better solution.
 */
public class TunnelUtil {

    // To be able to kill a tunnel by destroying its process, this command isn't run in background "-f" ssh option
    private static final String startTunnelCommand = "ssh -N -i %s -D %s %s";
    private static final String killAllTunnelsCommand = "pkill ssh";

    public static Process startTunnel(int port, String host, String sshkeyFile) throws IOException, InterruptedException {
        return executeBashCommand(String.format(startTunnelCommand, sshkeyFile, port, host).split(" "));
    }

    public static Process killAllTunnels() throws IOException, InterruptedException {
        return executeBashCommand(killAllTunnelsCommand.split(" "));
    }

    private static Process executeBashCommand(String[] commands) throws IOException, InterruptedException {
        return new ProcessBuilder(commands)
                .inheritIO()
                .directory(new File(System.getProperty("user.home")))
                .start();
    }

    public static void main(String[] args) throws Exception {
        Process p = startTunnel(8080, "ubuntu@18.221.127.190", "keys/ohiokey.pem");
        Thread.sleep(10000);
        p.destroy();
    }
}
