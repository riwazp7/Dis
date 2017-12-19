package impl.proxy.local;

import com.google.common.util.concurrent.ListenableFuture;
import generated.grpc.radio.ReMapPath;
import generated.grpc.radio.ReMapRequest;
import generated.grpc.radio.ScheduleReMapResponse;
import impl.aws.ProxyInstancesManager;
import impl.proxy.TunnelManager;
import impl.proxy.TunnelUtil;
import impl.proxy.radio.RadioHq;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    private static final String sshKeyFile = "~/keys/ohiokey.pem";

    private final TunnelManager tunnelManager;
    private final ProxyInstancesManager proxyInstancesManager;
    private final RadioHq hq = new RadioHq("18.220.36.129", DEF_HQ_PORT);
    private final ScheduledExecutorService backgroundExecutor = Executors.newSingleThreadScheduledExecutor();
    private int reqId = 0;
    private boolean currA = false;

    public ClientManager() {
        this.tunnelManager = new TunnelManager();
        this.proxyInstancesManager = new ProxyInstancesManager();
    }

    public void start() throws Exception {
        proxyInstancesManager.start();
        backgroundExecutor.scheduleAtFixedRate(this::changePath, 0, CHANGE_PATH_SECS, TimeUnit.SECONDS);
    }

    private void changePath() {

        List<String> newPath = generateRandomPath(DEF_PATH_LEN, proxyInstancesManager.getAliveProxies());
        String first = newPath.remove(0);
        ReMapPath.Builder reMapPath = ReMapPath.newBuilder();
        reMapPath.addAllMap(newPath);
        reMapPath.setReMapRequest(ReMapRequest.newBuilder().setMapId(reqId).build());
        ListenableFuture<ScheduleReMapResponse> responseListenableFuture = hq.sendReMapPath(reMapPath.build());
        try {
           ScheduleReMapResponse response = responseListenableFuture.get(30, TimeUnit.SECONDS);
           if (!response.getOk()) {
               System.out.println("Path setup failed in node: " + response.getFailedPeer());
               // Request failed.
               return;
           }
            ReMapRequest req = ReMapRequest.newBuilder().setMapId(reqId).build();
            reqId += 1;
            hq.excuteReMap(req);
            tunnelManager.setUpTunnel(8080, first);
            if (currA)
            TunnelUtil.bridgePort(6666, 8080);

        } catch (Exception e) {
            e.printStackTrace();
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
