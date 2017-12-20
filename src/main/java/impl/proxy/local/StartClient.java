package impl.proxy.local;

/**
 * Main class to call to start the client portion of the VPN.
 */
public class StartClient {

    public static void main(String[] args) throws Exception {
        new ClientManager().start();
    }

}
