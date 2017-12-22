package impl.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;

/**
 * Handles SOCKS5 tunnel starting and stopping information.
 */
public class TunnelManager {

    private static final Logger log = LoggerFactory.getLogger(TunnelManager.class.getSimpleName());

    private static final String keyLocation = "keys/ohiokey.pem";

    private final HashMap<String, Process> tunnels;

    public TunnelManager() {
        tunnels = new HashMap<>();
    }

    public void setUpTunnel(int fromPort, String toHostIp) throws IOException {
        tunnels.put(toHostIp, TunnelUtil.startTunnel(fromPort, "ubuntu@" + toHostIp, keyLocation));
    }

    public void closeTunnel(String toHostIp) {
        tunnels.remove(toHostIp).destroy();
    }

    public void refresh() throws IOException {
        TunnelUtil.refresh();
    }
}
