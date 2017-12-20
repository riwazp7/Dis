package impl.proxy.radio;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import generated.grpc.radio.ExecuteReMapResponse;
import generated.grpc.radio.ReMapPath;
import generated.grpc.radio.ReMapRequest;
import generated.grpc.radio.ScheduleReMapResponse;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

/**
 * The server for incoming gRPC requests to schedule and execute remaps.
 */
public class RadioListener {

    private final Server server;
    private List<String> peers;

    public RadioListener(int port,
                         Function<ReMapPath, ScheduleReMapResponse> pathConsumer,
                         Function<ReMapRequest, ExecuteReMapResponse> requestConsumer,
                         Runnable refreshRunnable) {
        this.server = ServerBuilder
                .forPort(port)
                .addService(new Services.ReMapService(pathConsumer, requestConsumer))
                .addService(new Services.RefresherService(refreshRunnable))
                .build();
    }

    public ListenableFuture<List<String>> requestPeers() {
        return Futures.immediateFuture(peers);
    }

    public void startServer() throws IOException {
        server.start();
        // remove when shutting down manually?
        Runtime.getRuntime().addShutdownHook(new Thread(RadioListener.this::shutDown));
    }

    public void shutDown() {
        server.shutdown();
    }
}
