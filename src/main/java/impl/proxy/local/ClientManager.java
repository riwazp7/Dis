package impl.proxy.local;

import impl.aws.ProxyInstancesManager;
import impl.proxy.TunnelManager;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientManager {

    private static final int CHANGE_PATH_SECS = 90;
    public static final int DEF_HQ_PORT = 8007;

    private final TunnelManager tunnelManager;
    private final ProxyInstancesManager proxyInstancesManager;
    private final ScheduledExecutorService backgroundExecutor = Executors.newSingleThreadScheduledExecutor();

    public ClientManager() {
        this.tunnelManager = new TunnelManager();
        this.proxyInstancesManager = new ProxyInstancesManager();
    }

    public void start() {
        // Set up initial tunnel and bridge.
        proxyInstancesManager.start();
        backgroundExecutor.scheduleAtFixedRate(this::changePath, 0, 90, TimeUnit.SECONDS);
    }

    private void changePath() {

    }

    private static List<String> generatePath(List<String> hosts) {

    }

}
