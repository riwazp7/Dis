package proxy;

import net.lightbody.bmp.mitm.CertificateInfo;
import net.lightbody.bmp.mitm.PemFileCertificateSource;
import net.lightbody.bmp.mitm.RootCertificateGenerator;
import net.lightbody.bmp.mitm.manager.ImpersonatingMitmManager;
import org.littleshoot.proxy.HostResolver;
import org.littleshoot.proxy.HttpProxyServerBootstrap;
import org.littleshoot.proxy.SslEngineSource;
import org.littleshoot.proxy.extras.SelfSignedSslEngineSource;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import vpn.impl.ProxyInstancesManager;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;

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
            return new InetSocketAddress(InetAddress.getByName(host), port);
            //return new InetSocketAddress("127.0.0.1", 443);
            //return proxyInstancesManager.getRandomProxy();
        }
    }

    private final HttpProxyServerBootstrap defaultHttpProxyServer;

    public ProxyServer(ProxyInstancesManager proxyInstancesManager) {
        this.defaultHttpProxyServer = DefaultHttpProxyServer.bootstrap().withPort(PORT)
                .withAllowRequestToOriginServer(true)
                .withAuthenticateSslClients(true)
                //.withSslEngineSource(new SelfSignedSslEngineSource())
                .withServerResolver(new Resolver(proxyInstancesManager));
    }

    public void start() {
        defaultHttpProxyServer.start();
    }

    public static void main(String[] args) throws Exception {
        PemFileCertificateSource source = new PemFileCertificateSource(
                new File("/Users/Riwaz/cert.cer"),
                new File("/Users/Riwaz/key.pem"),
                "password");
        ImpersonatingMitmManager manager = ImpersonatingMitmManager.builder()
                .rootCertificateSource(source)
                .trustAllServers(true)
                .build();
        DefaultHttpProxyServer.bootstrap().withManInTheMiddle(manager)
                .withAddress(new InetSocketAddress("localhost", 9091))
                .withAllowRequestToOriginServer(true)
                .withServerResolver(new Resolver(null))
                .start();
    }
}

