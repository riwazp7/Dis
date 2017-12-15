package impl.proxy.radio;

import com.google.common.util.concurrent.ListenableFuture;
import generated.grpc.radio.RefreshRequest;
import generated.grpc.radio.RefreshResponse;
import generated.grpc.radio.RefresherGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import impl.aws.ProxyInstancesManager;

import java.io.IOException;

public class RadioHq {

    private final ManagedChannel channel;
    private final RefresherGrpc.RefresherFutureStub refresherStub;
    private final Server server;
    private final ProxyInstancesManager proxyInstancesManager;

    public RadioHq(ProxyInstancesManager proxyInstancesManager, String host, int port) {
        this(proxyInstancesManager, ManagedChannelBuilder.forAddress(host, port).build(), port);
    }

    private RadioHq(ProxyInstancesManager proxyInstancesManager, ManagedChannel channel, int serverPort) {
        this.channel = channel;
        this.refresherStub = RefresherGrpc.newFutureStub(channel);
        this.proxyInstancesManager = proxyInstancesManager;
        this.server = ServerBuilder
                .forPort(serverPort)
                .addService(new Services.PeersService(
                        () -> String.join(" ", proxyInstancesManager.getAliveProxies())))
                .build();
    }

    public void shutDown() {
        channel.shutdown();
        server.shutdown();
    }

    public void startServer() throws IOException {
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(RadioHq.this::shutDown)); // remove when shutdown manually?
    }

    // Is this needed?
    public ListenableFuture<RefreshResponse> sendRefresher() {
        return refresherStub.refresh(RefreshRequest.newBuilder().build());
    }

    public static void main(String[] args) {

    }
}
