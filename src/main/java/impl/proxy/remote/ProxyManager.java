package impl.proxy.remote;

import generated.grpc.radio.ExecuteReMapResponse;
import generated.grpc.radio.ReMapPath;
import generated.grpc.radio.ReMapRequest;
import generated.grpc.radio.ScheduleReMapResponse;
import impl.proxy.ReMapHandler;
import impl.proxy.TunnelManager;
import impl.proxy.radio.RadioHq;
import impl.proxy.radio.RadioListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;

/**
 * Manages the VPN service in the proxy machines.
 * Responsible to respond to set-up path response.
 */
public class ProxyManager {

    private static final Logger log = LoggerFactory.getLogger(ProxyManager.class.getSimpleName());

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
    @Nullable
    private ExecuteReMapResponse executeReMap(@Nullable  ReMapRequest reMapRequest) {
        if (reMapRequest == null) {
            log.error("Received null ReMapRequest to execute.");
            return null;
        }
        log.info("Received ReMapRequest: ", reMapRequest.getMapId());
        return null;
    }

    /**
     * Refresh all tunnels to clear possible tunnels existing as daemons.
     */
    private void refresh() {
        try {

            tunnelManager.refresh();
        } catch (IOException e) {
            log.error("Refresh failed with exception", e);
        }
    }

}
