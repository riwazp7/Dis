package proxy;

import org.littleshoot.proxy.HostResolver;
import org.littleshoot.proxy.HttpProxyServerBootstrap;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import vpn.impl.ProxyInstancesManager;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class ProxyServer {

    private static final int PORT = 6666;

    private static class Resolver implements HostResolver {

        private final ProxyInstancesManager proxyInstancesManager;

        public Resolver(ProxyInstancesManager proxyInstancesManager) {
            this.proxyInstancesManager = proxyInstancesManager;
        }

        @Override
        public InetSocketAddress resolve(String host, int port) throws UnknownHostException {
            System.out.println(host + " " + port);
            return new InetSocketAddress("127.0.0.1", 8080);
            //return proxyInstancesManager.getRandomProxy();
        }
    }

    private final HttpProxyServerBootstrap defaultHttpProxyServer;

    public ProxyServer(ProxyInstancesManager proxyInstancesManager) {
        this.defaultHttpProxyServer = DefaultHttpProxyServer.bootstrap().withPort(PORT)
                .withServerResolver(new Resolver(proxyInstancesManager));
    }

    public void start() {
        defaultHttpProxyServer.start();
    }

    public static void main(String[] args) {
        ProxyServer server = new ProxyServer(null);
        server.start();
    }
}

