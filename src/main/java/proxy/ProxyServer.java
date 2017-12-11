package proxy;

import org.littleshoot.proxy.HostResolver;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Random;

public class ProxyServer {

    private static final int PORT = 8088;

    private static class Resolver implements HostResolver {

        private static final Random rdm = new Random();

        @Override
        public InetSocketAddress resolve(String host, int port) throws UnknownHostException {
            System.out.println(host);
            System.out.println(port);

            if (rdm.nextBoolean()) return new InetSocketAddress(host, port);
            return new InetSocketAddress("127.0.0.1", 8081);
        }
    }

    public static void main(String[] args) {
        DefaultHttpProxyServer.bootstrap()
                .withPort(PORT)
                .withAllowLocalOnly(true)
                .withServerResolver(new Resolver())
                .start();
    }
}

