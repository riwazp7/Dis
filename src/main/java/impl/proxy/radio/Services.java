package impl.proxy.radio;

import generated.grpc.radio.*;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.Callable;
import java.util.function.Function;

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

        private final Function<ReMapPath, ScheduleReMapResponse> reMapRequestConsumer;
        private final Function<ReMapRequest ,ExecuteReMapResponse> executeReMapConsumer;

        public ReMapService(
                Function<ReMapPath, ScheduleReMapResponse> reMapRequestConsumer,
                Function<ReMapRequest, ExecuteReMapResponse> executeReMapConsumer) {
            this.reMapRequestConsumer = reMapRequestConsumer;
            this.executeReMapConsumer = executeReMapConsumer;
        }

        @Override
        public void scheduleReMap(ReMapPath reMapPath, StreamObserver<ScheduleReMapResponse> responseObserver) {
            System.out.println("Schedule Remap grpc call received");
            responseObserver.onNext(reMapRequestConsumer.apply(reMapPath)); // Change to callable and return response
            responseObserver.onCompleted();
        }

        @Override
        public void executeReMap(ReMapRequest request, StreamObserver<ExecuteReMapResponse> responseObserver) {
            System.out.println("Execute Remap grpc call received");
            responseObserver.onNext(executeReMapConsumer.apply(request)); // Change to callable and return response
            responseObserver.onCompleted();
        }
    }

}
