package impl.proxy.local;

import generated.grpc.radio.ReMapRequest;
import impl.aws.ProxyInstancesManager;
import impl.proxy.TunnelManager;
import impl.proxy.TunnelUtil;
import impl.proxy.radio.RadioHq;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientManager {

    private static final int CHANGE_PATH_SECS = 90;
    public static final int DEF_HQ_PORT = 8999;

    private final TunnelManager tunnelManager;
    private final ProxyInstancesManager proxyInstancesManager;
    private final RadioHq hq = new RadioHq("18.220.36.129", DEF_HQ_PORT);
    private final ScheduledExecutorService backgroundExecutor = Executors.newSingleThreadScheduledExecutor();

    public ClientManager() {
        this.tunnelManager = new TunnelManager();
        this.proxyInstancesManager = new ProxyInstancesManager();
    }

    public void start() throws Exception {
        proxyInstancesManager.start();
        backgroundExecutor.scheduleAtFixedRate(this::changePath, 0, 90, TimeUnit.SECONDS);
    }

    private void changePath() {
        try {

        } catch (IOException e) {

        }

        while (true) {
            TunnelUtil.bridgePort(6666, 8080);
            TunnelUtil.startTunnel(8080, "ubuntu@18.220.36.129", "~/keys/ohiokey.pem");
            hq.excuteReMap(ReMapRequest.newBuilder().build());
            Thread.sleep(10000);
        }
    }

    private static List<String> generateRandomPath(int pathLength, List<String> hosts) {
        if (hosts.size() < pathLength) {
            throw new RuntimeException("Path is longer than number of hosts available");
        }
        Collections.shuffle(hosts);
        return hosts.subList(0, pathLength);
    }

}
