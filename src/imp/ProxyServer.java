package imp;


import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import java.net.InetSocketAddress;

public class ProxyServer {

    private static final int PORT = 8080;

    public static void main(String[] args) {
        DefaultHttpProxyServer.bootstrap()
                .withPort(PORT)
                .withAllowLocalOnly(true)
                .withServerResolver(InetSocketAddress::new)
                .start();
    }

}
