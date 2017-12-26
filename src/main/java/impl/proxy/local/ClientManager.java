package impl.proxy.local;

import generated.grpc.radio.ReMapPath;
import impl.proxy.TunnelManager;
import impl.proxy.radio.RadioHq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Manages the VPN instance in a client.
 */
public class ClientManager {

    private static final Logger log = LoggerFactory.getLogger(ClientManager.class.getSimpleName());

    /**
     * Change path every this many seconds.
     */
    private static final int CHANGE_PATH_SECS = 90;

    public static final int DEF_HQ_PORT = 8999;

    private final TunnelManager tunnelManager;
    // private final ProxyInstancesManager proxyInstancesManager;
    private final ScheduledExecutorService backgroundExecutor = Executors.newSingleThreadScheduledExecutor();

    public ClientManager() {
        this.tunnelManager = new TunnelManager();
        // this.proxyInstancesManager = new ProxyInstancesManager();
    }

    public void start() throws Exception {
        // proxyInstancesManager.start();
        backgroundExecutor.scheduleAtFixedRate(this::changePath, 0, CHANGE_PATH_SECS, TimeUnit.SECONDS);
    }

    private void changePath() {

    }

    public static void main(String[] args) throws Exception {

        RadioHq hq = new RadioHq("52.224.223.144", DEF_HQ_PORT);
        ReMapPath reMapPath = ReMapPath.newBuilder().build();
        System.out.println(hq.sendReMapPath(reMapPath).get());

    }

}
