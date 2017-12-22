package impl.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;

public class ReMapHandler {

    private static final Logger log = LoggerFactory.getLogger(ReMapHandler.class.getSimpleName());

    private static final int PORT_A = 6767;
    private static final int PORT_B = 6768;

    private boolean aActive = false;
    private final TunnelManager tunnelManager;

    @Nullable
    private Process portAProcess = null;

    @Nullable
    private Process portBProcess = null;

    public ReMapHandler() {
        this.tunnelManager = new TunnelManager();
    }

    public void scheduleReMap(@Nullable String nextHost) throws IOException {
        if (nextHost == null) {
            return;
        }
        int nextPort;
        if (aActive) {
            nextPort = PORT_B;
            if (portBProcess != null) portBProcess.destroyForcibly();
        } else {
            nextPort = PORT_A;
            if (portAProcess != null) portAProcess.destroyForcibly();
        }
        tunnelManager.setUpTunnel(nextPort, nextHost);
    }

    public void execute() {
        aActive = !aActive;
    }
}
