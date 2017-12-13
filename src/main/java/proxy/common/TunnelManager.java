package proxy.common;

import java.io.IOException;
import java.util.HashMap;

public class TunnelManager {

    private static final String keyLocation = "keys/ohiokey.pem";

    private final HashMap<String, Process> tunnels;

    public TunnelManager() {
        tunnels = new HashMap<>();
    }

    public void setUpTunnel(int fromPort, String toHostIp) throws IOException {
        tunnels.put(toHostIp, TunnelUtil.startTunnel(fromPort, toHostIp, keyLocation));
    }

    public void closeTunnel(String toHostIp) {
        tunnels.remove(toHostIp).destroy();
    }

    public void stop() throws IOException {
        TunnelUtil.killAllTunnels();
    }
}
