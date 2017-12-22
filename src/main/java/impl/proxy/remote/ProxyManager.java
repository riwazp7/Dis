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
        return null;
    }

    /**
     * Actually execute a ReMap request that was received.
     */
    private ExecuteReMapResponse executeReMap(@Nullable ReMapRequest reMapRequest) {
       return null;
    }

    /**
     * Refresh all tunnels to clear possible tunnels existing as daemons.
     */
    private void refresh() {
    }

}
