package impl.proxy.remote;

import impl.proxy.local.ClientManager;

public class StartProxy {

    private static final String HQ_IP = "";

    public static void main(String[] args) throws Exception {
        new ProxyManager(HQ_IP, ClientManager.DEF_HQ_PORT).start();
    }

}

