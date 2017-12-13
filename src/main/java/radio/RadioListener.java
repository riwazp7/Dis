package radio;

import com.google.common.util.concurrent.Futures;
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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RadioListener {

    private final Server server;
    private final ManagedChannel channel;
    private final PeersGrpc.PeersFutureStub peersFutureStub;
    private final Executor backgroundExecutor;

    public RadioListener(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).build(), port);
    }

    private RadioListener(ManagedChannel channel, int serverPort) {
        this.channel = channel;
        this.server = ServerBuilder.forPort(serverPort).addService(new TerminatorService()).build();
        this.peersFutureStub = PeersGrpc.newFutureStub(channel);
        this.backgroundExecutor = Executors.newSingleThreadExecutor();
    }

    public void startServer() throws IOException {
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(RadioListener.this::shutDown)); // remove when shutdown manually?
    }

    public void shutDown() {
        server.shutdown();
        channel.shutdown();
    }

    public ListenableFuture<List<String>> requestPeers() {
        ListenableFuture<PeersResponse> response  = peersFutureStub.getPeers(PeerRequest.newBuilder().build());
        return Futures.transformAsync(response, peersResponse -> {
            if (peersResponse != null) {
               return Futures.immediateFuture(Arrays.asList(peersResponse.getPeers().split(" ")));
            }
            return null;
        }, backgroundExecutor);
    }

    private static class TerminatorService extends TerminatorGrpc.TerminatorImplBase {
        @Override
        public void terminate(TerminateRequest request, StreamObserver<TerminateResponse> responseObserver) {
            responseObserver.onNext(TerminateResponse.newBuilder().build());
            responseObserver.onCompleted();
        }
    }
}
