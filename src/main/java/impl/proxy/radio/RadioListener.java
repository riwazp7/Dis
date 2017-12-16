package impl.proxy.radio;

import generated.grpc.radio.ExecuteReMapResponse;
import generated.grpc.radio.ReMapPath;
import generated.grpc.radio.ReMapRequest;
import generated.grpc.radio.ScheduleReMapResponse;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.function.Function;

public class RadioListener {

    private final Server server;

    public RadioListener(int port,
                         Function<ReMapPath, ScheduleReMapResponse> pathConsumer,
                         Function<ReMapRequest, ExecuteReMapResponse> requestConsumer,
                         Runnable refreshRunnable) {
        this.server = ServerBuilder
                .forPort(port)
                .addService(new Services.ReMapService(pathConsumer, requestConsumer))
                .build();
    }

    public void startServer() throws IOException {
        server.start();
        // remove when shutting down manually?
        Runtime.getRuntime().addShutdownHook(new Thread(RadioListener.this::shutDown));
    }

    public void shutDown() {
        server.shutdown();
    }
}
