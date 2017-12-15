package impl.proxy.remote;

import generated.grpc.radio.ReMapPath;
import generated.grpc.radio.ReMapRequest;
import impl.proxy.TunnelManager;
import impl.proxy.radio.RadioListener;

import java.io.IOException;

public class ProxyManager {

    private static final int portA = 7766;
    private static final int portB = 7767;

    private final TunnelManager tunnelManager;
    private final RadioListener radioListener;
    private final int hqPort; // where to contact server
    private final String hqAddress;

    public ProxyManager(String hqAddress, int hqPort) {
        this.hqAddress = hqAddress;
        this.hqPort = hqPort;
        this.tunnelManager = new TunnelManager();
        this.radioListener = new RadioListener(hqAddress, hqPort, this::handleReMap, this::executeReMap, this::refresh);
    }

    public void start() throws IOException, InterruptedException {
        radioListener.startServer();
        Thread.sleep(Long.MAX_VALUE);
    }

    private void handleReMap(ReMapPath reMapPath) {
        String[] path = reMapPath.getMapList().toArray(new String[0]);
    }

    private void executeReMap(ReMapRequest reMapRequest) {

    }

    private void refresh() {
        try {
            tunnelManager.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
