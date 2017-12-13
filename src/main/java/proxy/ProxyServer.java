package proxy;

import com.jcraft.jsch.ChannelDirectTCPIP;
import com.jcraft.jsch.ChannelForwardedTCPIP;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.littleshoot.proxy.HostResolver;
import org.littleshoot.proxy.HttpProxyServerBootstrap;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import vpn.impl.ProxyInstancesManager;

import java.io.InputStream;
import java.io.PipedInputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
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

    public static void main(String[] args) throws Exception {
        JSch jSch = new JSch();
        jSch.addIdentity("/Users/Riwaz/keys/ohiokey.pem");
        Session session = jSch.getSession("ubuntu","18.221.127.190", 22);
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPortForwardingL(8080, "localhost", 80);
        session.connect();
        System.out.println("Connected");
        //ChannelForwardedTCPIP channel = (ChannelForwardedTCPIP) session.openChannel("direct-tcpip");


        // Socket socket = new Socket();
        // socket.connect(new InetSocketAddress("localhost", 8080));


        //InputStream inputStream = new PipedInputStream();
        //channel.setInputStream(inputStream);
        // inputStream.transferTo(socket.getOutputStream());


//
//
//        Session another = jSch.getSession("<user>", "localhost", 8080);
//        another.setPortForwardingL(6666, "127.0.0.1", )
//        session.connect();

//        Session session = jSch.getSession("ubuntu","18.221.127.190", 22);
//        session.setConfig("StrictHostKeyChecking", "no");
//        session.setPortForwardingL(6666, "127.0.0.1", )
//
//        Session forwarding = jSch.getSession("<user>", "127.0.0.1", 8080);
//        forwarding.setPortForwardingL()
//
//        session.setPortForwardingL(8080, "18.221.127.190", 22);
//        session.connect();
//        session.openChannel("direct-tcpip");
//
//        jSch.addIdentity("/Users/Riwaz/keys/ohiokey.pem");


    }
}

