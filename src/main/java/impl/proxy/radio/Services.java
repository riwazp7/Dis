package impl.proxy.radio;

import generated.grpc.radio.ExecuteReMapResponse;
import generated.grpc.radio.PeerRequest;
import generated.grpc.radio.PeersGrpc;
import generated.grpc.radio.PeersResponse;
import generated.grpc.radio.ReMapPath;
import generated.grpc.radio.ReMapRequest;
import generated.grpc.radio.RemapGrpc;
import generated.grpc.radio.ScheduleReMapResponse;
import generated.grpc.radio.TerminateRequest;
import generated.grpc.radio.TerminateResponse;
import generated.grpc.radio.TerminatorGrpc;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

class Services {
     static class TerminatorService extends TerminatorGrpc.TerminatorImplBase {
        @Override
        public void terminate(TerminateRequest request, StreamObserver<TerminateResponse> responseObserver) {
            responseObserver.onNext(TerminateResponse.newBuilder().build());
            responseObserver.onCompleted();
        }
    }

    static class PeersService extends PeersGrpc.PeersImplBase {

        private final Callable<String> instancesProviderCallable;

        PeersService(Callable<String> instancesProviderCallable) {
            super();
            this.instancesProviderCallable = instancesProviderCallable;
        }

        @Override
        public void getPeers(PeerRequest request, StreamObserver<PeersResponse> responseObserver) {
            try {
                responseObserver.onNext(PeersResponse.newBuilder().setPeers(instancesProviderCallable.call()).build());
                responseObserver.onCompleted();
            } catch (Exception e) {
                throw new RuntimeException("Exception retrieving peers for remote call: ", e);
            }
        }
    }

    static class ReMapService extends RemapGrpc.RemapImplBase {

        private final Consumer<ReMapPath> reMapRequestConsumer;
        private final Consumer<ReMapRequest> executeReMapConsumer;

        public ReMapService(Consumer<ReMapPath> reMapRequestConsumer, Consumer<ReMapRequest> executeReMapConsumer) {
            this.reMapRequestConsumer = reMapRequestConsumer;
            this.executeReMapConsumer = executeReMapConsumer;
        }

        @Override
        public void scheduleReMap(ReMapPath reMapPath, StreamObserver<ScheduleReMapResponse> responseObserver) {
            reMapRequestConsumer.accept(reMapPath);
        }

        @Override
        public void executeReMap(ReMapRequest request, StreamObserver<ExecuteReMapResponse> responseObserver) {
            executeReMapConsumer.accept(request);
        }
    }

}
