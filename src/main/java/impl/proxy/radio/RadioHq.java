package impl.proxy.radio;

import com.google.common.util.concurrent.ListenableFuture;
import generated.grpc.radio.ExecuteReMapResponse;
import generated.grpc.radio.ReMapPath;
import generated.grpc.radio.ReMapRequest;
import generated.grpc.radio.RefreshRequest;
import generated.grpc.radio.RefreshResponse;
import generated.grpc.radio.RefresherGrpc;
import generated.grpc.radio.RemapGrpc;
import generated.grpc.radio.ScheduleReMapResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * The client to talk to gRPC servers set up on proxy machines.
 * Has methods to send a remap, execute a remap, and refresh all tunnels in the proxies in case of an error.
 */
public class RadioHq {

    private final ManagedChannel channel;
    private final RefresherGrpc.RefresherFutureStub refresherStub;
    private final RemapGrpc.RemapFutureStub remapFutureStub;

    public RadioHq(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext(true).build());
    }

    private RadioHq(ManagedChannel channel) {
        this.channel = channel;
        this.refresherStub = RefresherGrpc.newFutureStub(channel);
        this.remapFutureStub = RemapGrpc.newFutureStub(channel);
    }

    public void shutDown() {
        channel.shutdown();
    }

    // Is this needed?
    public ListenableFuture<RefreshResponse> sendRefresher() {
        return refresherStub.refresh(RefreshRequest.newBuilder().build());
    }

    public ListenableFuture<ScheduleReMapResponse> sendReMapPath(ReMapPath reMapPath) {
        return remapFutureStub.scheduleReMap(reMapPath);
    }

    public ListenableFuture<ExecuteReMapResponse> excuteReMap(ReMapRequest request) {
        return remapFutureStub.executeReMap(request);
    }

    public static void main(String[] args) {

    }
}
