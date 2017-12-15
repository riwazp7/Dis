package impl.proxy.local;

import generated.grpc.radio.ReMapPath;
import generated.grpc.radio.ReMapRequest;
import impl.aws.ProxyInstancesManager;
import impl.proxy.TunnelManager;
import impl.proxy.TunnelUtil;
import impl.proxy.radio.RadioHq;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ClientManager {

    private static final int CHANGE_PATH_SECS = 90;
    public static final int DEF_HQ_PORT = 8007;

    private static final ReMapPath path1 = ReMapPath.newBuilder().addMap("18.217.100.180").build();

    private final TunnelManager tunnelManager;
    private final ProxyInstancesManager proxyInstancesManager;
    private final RadioHq hq = new RadioHq("52.15.79.235", DEF_HQ_PORT);
    private final ScheduledExecutorService backgroundExecutor = Executors.newSingleThreadScheduledExecutor();

    public ClientManager() {
        this.tunnelManager = new TunnelManager();
        this.proxyInstancesManager = new ProxyInstancesManager();
    }

    public void start() throws Exception {
        // Set up initial tunnel and bridge.
        proxyInstancesManager.start();
        changePath();
        // backgroundExecutor.scheduleAtFixedRate(this::changePath, 0, 90, TimeUnit.SECONDS);
    }

    private void changePath() throws Exception {
        System.out.println(hq.sendReMapPath(path1));
        TunnelUtil.bridgePort(6666, 8080);
        TunnelUtil.startTunnel(8080, "52.15.79.235","/Users/Riwaz/Keys/ohiokey.pem");
        hq.excuteReMap(ReMapRequest.newBuilder().build());
    }

    private static List<String> generatePath(List<String> hosts) {
        return null;
    }

}
