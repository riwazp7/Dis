package radio;

import com.google.common.util.concurrent.ListenableFuture;
import generated.grpc.radio.PeerRequest;
import generated.grpc.radio.PeersGrpc;
import generated.grpc.radio.PeersResponse;
import generated.grpc.radio.TerminateRequest;
import generated.grpc.radio.TerminateResponse;
import generated.grpc.radio.TerminatorGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import vpn.impl.ProxyInstancesManager;

import java.io.IOException;

public class RadioHq {

    private final ManagedChannel channel;
    private final TerminatorGrpc.TerminatorFutureStub terminatorStub;
    private final Server server;
    private final ProxyInstancesManager proxyInstancesManager;

    public RadioHq(ProxyInstancesManager proxyInstancesManager, String host, int port) {
        this(proxyInstancesManager, ManagedChannelBuilder.forAddress(host, port).build(), port);
    }

    private RadioHq(ProxyInstancesManager proxyInstancesManager, ManagedChannel channel, int serverPort) {
        this.channel = channel;
        this.terminatorStub = TerminatorGrpc.newFutureStub(channel);
        this.proxyInstancesManager = proxyInstancesManager;
        this.server = ServerBuilder
                .forPort(serverPort)
                .addService(new PeersService(proxyInstancesManager)).build();
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
    public ListenableFuture<TerminateResponse> sendTerminate() {
        return terminatorStub.terminate(TerminateRequest.newBuilder().build());
    }

    private static class PeersService extends PeersGrpc.PeersImplBase {

        private final ProxyInstancesManager proxyInstancesManager;

         PeersService(ProxyInstancesManager proxyInstancesManager) {
            super();
            this.proxyInstancesManager = proxyInstancesManager;
        }

        @Override
        public void getPeers(PeerRequest request, StreamObserver<PeersResponse> responseObserver) {
            String response = String.join(" ", proxyInstancesManager.getAliveProxies());
            responseObserver.onNext(PeersResponse.newBuilder().setPeers(response).build());
            responseObserver.onCompleted();
        }
    }

    public static void main(String[] args) {

    }
}
