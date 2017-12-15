package impl.proxy.radio;

import generated.grpc.radio.ReMapPath;
import generated.grpc.radio.ReMapRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.function.Consumer;

public class RadioListener {

    private final Server server;
    private final ManagedChannel channel;

    public RadioListener(String host,
                         int port,
                         Consumer<ReMapPath> pathConsumer,
                         Consumer<ReMapRequest> requestConsumer,
                         Runnable refreshRunnable) {
        this(ManagedChannelBuilder.forAddress(host, port).build(), port, pathConsumer, requestConsumer, refreshRunnable);
    }

    private RadioListener(ManagedChannel channel,
                          int serverPort,
                          Consumer<ReMapPath> pathConsumer,
                          Consumer<ReMapRequest> requestConsumer,
                          Runnable refreshRunnable) {
        this.channel = channel;
        this.server = ServerBuilder
                .forPort(serverPort)
                .addService(new Services.RefresherService(refreshRunnable))
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
        channel.shutdown();
    }
}
