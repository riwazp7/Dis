import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpServerConnection;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.protocol.*;

import java.net.ServerSocket;

public class ProxyServer {

    private static final int BUF_SIZE = 128 * 1024;

    private HttpService service;
    private HttpServerConnection connection;
    private HttpContext httpContext;

    private ProxyServer() throws Exception {
        UriHttpRequestHandlerMapper mapper = new UriHttpRequestHandlerMapper();
        mapper.register("*", getHandler());
        service = new HttpService(HttpProcessorBuilder.create().build(), mapper);
        connection = getServerConnection();
        httpContext = new BasicHttpContext();
    }

    public void start() throws Exception {
        try {
            while (connection.isOpen()) {
                service.handleRequest(connection, httpContext);
            }
        } finally {
            connection.shutdown();
        }
    }

    private static HttpRequestHandler getHandler() throws Exception {
        return new HttpRequestHandler() {
            @Override
            public void handle(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) {

            }
        };
    }

    private static HttpServerConnection getServerConnection() throws Exception {
        DefaultBHttpServerConnection connection = new DefaultBHttpServerConnection(BUF_SIZE);
        ServerSocket socket = new ServerSocket(8888);
        connection.bind(socket.accept());
        return connection;
    }

}
