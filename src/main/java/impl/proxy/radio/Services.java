package impl.proxy.radio;

import generated.grpc.radio.*;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

class Services {
    static class RefresherService extends RefresherGrpc.RefresherImplBase {

        private final Runnable refreshRunnable;

        public RefresherService(Runnable runnable) {
            this.refreshRunnable = runnable;
        }

        @Override
        public void refresh(RefreshRequest request, StreamObserver<RefreshResponse> responseObserver) {
            refreshRunnable.run();
            responseObserver.onNext(RefreshResponse.newBuilder().build());
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
            reMapRequestConsumer.accept(reMapPath); // Change to callable and return response
        }

        @Override
        public void executeReMap(ReMapRequest request, StreamObserver<ExecuteReMapResponse> responseObserver) {
            executeReMapConsumer.accept(request); // Change to callable and return response
        }
    }

}
