package impl.proxy.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class to call to start the client portion of the VPN.
 */
public class StartClient {

    private static final Logger log = LoggerFactory.getLogger(StartClient.class);

    public static void main(String[] args) throws Exception {
        new ClientManager().start();
        log.info("Started Client");
    }

}
