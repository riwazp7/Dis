package impl.proxy.remote;

import impl.proxy.radio.RadioListener;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PeersManager {

    private final RadioListener listener;
    private final ScheduledExecutorService scheduledExecutor;
    private List<String> peers;

    public PeersManager(RadioListener listener) {
        this.listener = listener;
        this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() throws TimeoutException {
        if (!updatePeerList()) {
            throw new TimeoutException();
        }
        scheduledExecutor.scheduleAtFixedRate(this::updatePeerList, 45, 45, TimeUnit.SECONDS);
    }

    private boolean updatePeerList() {
        Future<List<String>> peersFuture = listener.requestPeers();
        if (peersFuture == null) {
            return false;
        }
        try {
            List<String> newPeers = peersFuture.get(10, TimeUnit.SECONDS);
            synchronized (this) {
                peers.clear();
                peers.addAll(newPeers);
            }
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            // Failed getting new peers. handle later.
            return false;
        }
        return true;
    }

    public synchronized List<String> getPeers() {
        return Collections.unmodifiableList(peers);
    }
}
