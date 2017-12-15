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

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ProxyManager {

    private final TunnelManager tunnelManager;
    private final RadioListener radioListener;
    private final int hqPort; // where to contact server
    private final String hqAddress;
    private final ReMapHandler reMapHandler;
    private RadioHq radioHq;

    public ProxyManager(String hqAddress, int hqPort) {
        this.hqAddress = hqAddress;
        this.hqPort = hqPort;
        this.tunnelManager = new TunnelManager();
        this.radioListener = new RadioListener(hqAddress, hqPort, this::handleReMap, this::executeReMap, this::refresh);
        this.reMapHandler = new ReMapHandler();
    }

    public void start() throws IOException, InterruptedException {
        radioListener.startServer();
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
                    return radioHq.sendReMapPath(newPath).get(10, TimeUnit.SECONDS);
                } catch (Exception e) {
                    e.printStackTrace();
                    return ScheduleReMapResponse.newBuilder().setOk(false)
                            .setFailedPeer(reMapPath.getMap(0)).build();
                }
            }
        } catch (IOException e) {
            //
        }
        return ScheduleReMapResponse.newBuilder().setFailedPeer("Hi").build();
    }

    private ExecuteReMapResponse executeReMap(ReMapRequest reMapRequest) {
        reMapHandler.execute();
        radioHq.excuteReMap(reMapRequest);
        return ExecuteReMapResponse.newBuilder().build();
    }

    private void refresh() {
        try {
            tunnelManager.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
