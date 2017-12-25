package impl.proxy.remote;

import impl.proxy.local.ClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main class to call to Start the proxy portion of the VPN. Takes the IP of the clint machine.
 */
public class StartProxy {

    private static final Logger log = LoggerFactory.getLogger(StartProxy.class.getSimpleName());

    private static final String DEF_HQ_IP = "137.165.165.150";

    private static String HQ_IP = DEF_HQ_IP;

    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            HQ_IP = args[0];
        }
        new ProxyManager(HQ_IP, ClientManager.DEF_HQ_PORT).start();
        log.info("Started Proxy for HQ_IP %s", HQ_IP);
    }
}

