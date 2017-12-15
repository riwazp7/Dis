package impl.proxy.radio;

import com.google.common.util.concurrent.ListenableFuture;
import generated.grpc.radio.RefreshRequest;
import generated.grpc.radio.RefreshResponse;
import generated.grpc.radio.RefresherGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class RadioHq {

    private final ManagedChannel channel;
    private final RefresherGrpc.RefresherFutureStub refresherStub;

    public RadioHq(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).build());
    }

    private RadioHq(ManagedChannel channel) {
        this.channel = channel;
        this.refresherStub = RefresherGrpc.newFutureStub(channel);
    }

    public void shutDown() {
        channel.shutdown();
    }

    // Is this needed?
    public ListenableFuture<RefreshResponse> sendRefresher() {
        return refresherStub.refresh(RefreshRequest.newBuilder().build());
    }

    public static void main(String[] args) {

    }
}
