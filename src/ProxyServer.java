import java.io.IOException;
import java.net.ServerSocket;

public class ProxyServer {

    private static final int PORT = 80;
    private static ServerSocket serverSocket = null;

    

    public void getServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
    }

}
