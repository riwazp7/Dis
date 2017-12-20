package impl.proxy.remote;

import generated.grpc.radio.ExecuteReMapResponse;
import generated.grpc.radio.ReMapPath;
import generated.grpc.radio.ReMapRequest;
import generated.grpc.radio.ScheduleReMapResponse;
import impl.proxy.ReMapHandler;
import impl.proxy.TunnelManager;
import impl.proxy.local.ClientManager;
import impl.proxy.radio.RadioHq;
import impl.proxy.radio.RadioListener;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Manages the VPN service in the proxy machines.
 * Responsible to respond to set-up path response.
 */
public class ProxyManager {

    private final TunnelManager tunnelManager;
    private final RadioListener radioListener;
    private final ReMapHandler reMapHandler;
    private RadioHq radioHq;

    public ProxyManager(String hqAddress, int hqPort) {
        this.tunnelManager = new TunnelManager();
        this.radioListener = new RadioListener(hqPort, this::handleReMap, this::executeReMap, this::refresh);
        this.reMapHandler = new ReMapHandler();
    }

    public void start() throws IOException, InterruptedException {
        radioListener.startServer();
        System.out.println("Radio listener server has been started");
        Thread.sleep(Long.MAX_VALUE);
    }

    private ScheduleReMapResponse handleReMap(ReMapPath reMapPath) {
        try {
            if (reMapPath.getMapCount() == 0) {
                reMapHandler.scheduleReMap(null);
            } else {
                radioHq = new RadioHq(reMapPath.getMap(0), ClientManager.DEF_HQ_PORT);
                List<String> it = reMapPath.getMapList().subList(1, reMapPath.getMapList().size());
                ReMapPath newPath = ReMapPath.newBuilder().addAllMap(it).build();
                try {
                    // Send a remap request to remaining machines down the chain and wait upto 20 secs to receive an OK.
                    return radioHq.sendReMapPath(newPath).get(20, TimeUnit.SECONDS);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Next peer failed.");
                    // Otherwise the next peer in the list is the one that failed.
                    return ScheduleReMapResponse.newBuilder().setOk(false)
                            .setFailedPeer(reMapPath.getMap(0)).build();
                }
            }
        } catch (IOException e) {
            // This peer failed.
        }
        // This proxy failed. Send IP of this proxy as response.
        return ScheduleReMapResponse.newBuilder()
                .setOk(false)
                .setFailedPeer(InetAddress.getLoopbackAddress()
                        .getHostAddress())
                .build();
    }

    /**
     * Actually execute a ReMap request that was received.
     */
    private ExecuteReMapResponse executeReMap(@Nullable ReMapRequest reMapRequest) {
        if (reMapRequest == null) return ExecuteReMapResponse.newBuilder().build();
        System.out.println("Execute ReMap Received: ");
        reMapHandler.execute();
        radioHq.excuteReMap(reMapRequest);
        return ExecuteReMapResponse.newBuilder().build();
    }

    /**
     * Refresh all tunnels to clear possible tunnels existing as daemons.
     */
    private void refresh() {
        try {
            tunnelManager.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
