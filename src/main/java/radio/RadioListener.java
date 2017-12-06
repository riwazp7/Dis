package radio;

import generated.grpc.radio.TerminateRequest;
import generated.grpc.radio.TerminateResponse;
import generated.grpc.radio.TerminatorGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

public class RadioListener {

    private Server server;
    private int port;

    public RadioListener(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        this.server = ServerBuilder.forPort(port).addService(new TerminatorService()).build().start();
        Runtime.getRuntime().addShutdownHook(new Thread(RadioListener.this::stop));
    }

    private void stop() {
        this.server.shutdown();
    }

    private static class TerminatorService extends TerminatorGrpc.TerminatorImplBase {
        @Override
        public void terminate(TerminateRequest request, StreamObserver<TerminateResponse> responseObserver) {
            responseObserver.onNext(TerminateResponse.newBuilder().build());
            responseObserver.onCompleted();
        }
    }

}
