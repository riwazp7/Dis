package impl.proxy.radio;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import generated.grpc.radio.PeerRequest;
import generated.grpc.radio.PeersGrpc;
import generated.grpc.radio.PeersResponse;
import generated.grpc.radio.ReMapPath;
import generated.grpc.radio.ReMapRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class RadioListener {

    private final Server server;
    private final ManagedChannel channel;
    private final PeersGrpc.PeersFutureStub peersFutureStub;
    private final Executor backgroundExecutor;

    public RadioListener(String host,
                         int port,
                         Consumer<ReMapPath> pathConsumer,
                         Consumer<ReMapRequest> requestConsumer,
                         Runnable refreshRunnable) {
        this(ManagedChannelBuilder.forAddress(host, port).build(), port, pathConsumer, requestConsumer, refreshRunnable);
    }

    private RadioListener(ManagedChannel channel,
                          int serverPort,
                          Consumer<ReMapPath> pathConsumer,
                          Consumer<ReMapRequest> requestConsumer,
                          Runnable refreshRunnable) {
        this.channel = channel;
        this.peersFutureStub = PeersGrpc.newFutureStub(channel);
        this.backgroundExecutor = Executors.newSingleThreadExecutor();
        this.server = ServerBuilder
                .forPort(serverPort)
                .addService(new Services.RefresherService(refreshRunnable))
                .addService(new Services.ReMapService(pathConsumer, requestConsumer))
                .build();
    }

    public void startServer() throws IOException {
        server.start();
        // remove when shutting down manually?
        Runtime.getRuntime().addShutdownHook(new Thread(RadioListener.this::shutDown));
    }

    public void shutDown() {
        server.shutdown();
        channel.shutdown();
    }

    @Nullable
    public ListenableFuture<List<String>> requestPeers() {
        ListenableFuture<PeersResponse> response  = peersFutureStub.getPeers(PeerRequest.newBuilder().build());
        return Futures.transformAsync(response, peersResponse -> {
            if (peersResponse != null) {
               return Futures.immediateFuture(Arrays.asList(peersResponse.getPeers().split(" ")));
            }
            return null;
        }, backgroundExecutor);
    }
}
