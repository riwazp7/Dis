package impl.proxy.remote;

import impl.proxy.local.ClientManager;

/**
 * The main class to call to Start the proxy portion of the VPN. Takes the IP of the clint machine.
 */
public class StartProxy {

    private static final String DEF_HQ_IP = "137.165.165.150";

    public static void main(String[] args) throws Exception {
        if (args.length > 0)
            new ProxyManager(args[0], ClientManager.DEF_HQ_PORT).start();

        new ProxyManager(DEF_HQ_IP, ClientManager.DEF_HQ_PORT).start();
    }

}

