package impl.proxy.local;

import impl.aws.ProxyInstancesManager;
import impl.proxy.TunnelManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Manages the VPN instance in a client.
 */
public class ClientManager {

    /**
     * Change path every this many seconds.
     */
    private static final int CHANGE_PATH_SECS = 90;

    /**
     * Default path length
     */
    private static final int DEF_PATH_LEN = 2;

    public static final int DEF_HQ_PORT = 8999;

    private final TunnelManager tunnelManager;
    private final ProxyInstancesManager proxyInstancesManager;
    private final ScheduledExecutorService backgroundExecutor = Executors.newSingleThreadScheduledExecutor();
    private int reqId = 0;
    private boolean currA = false;

    public ClientManager() {
        this.tunnelManager = new TunnelManager();
        this.proxyInstancesManager = new ProxyInstancesManager();
    }

    public void start() throws Exception {
        // proxyInstancesManager.start();

        backgroundExecutor.scheduleAtFixedRate(this::changePath, 0, CHANGE_PATH_SECS, TimeUnit.SECONDS);
    }

    private void changePath() {

    }
}
