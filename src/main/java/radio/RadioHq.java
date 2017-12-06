package radio;

import com.google.common.util.concurrent.ListenableFuture;
import generated.grpc.radio.TerminateRequest;
import generated.grpc.radio.TerminateResponse;
import generated.grpc.radio.TerminatorGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class RadioHq {

    private ManagedChannel channel;
    private TerminatorGrpc.TerminatorFutureStub terminatorStub;

    public RadioHq(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).build());
    }

    private RadioHq(ManagedChannel channel) {
        this.channel = channel;
        this.terminatorStub = TerminatorGrpc.newFutureStub(channel);
    }

    public void shutDown() throws InterruptedException {
        channel.shutdown();
    }

    private ListenableFuture<TerminateResponse> sendTerminate() {
        return terminatorStub.terminate(TerminateRequest.newBuilder().build());
    }

    public static void main(String[] args) {

    }

}
