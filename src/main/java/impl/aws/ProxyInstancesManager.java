package impl.aws;

import api.InstanceFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * A manager to start, stop, and query the static instance factory.
 */
public class ProxyInstancesManager {

    private AwsManager awsManager;
    private InstanceFactory instanceFactory;

    public ProxyInstancesManager() {
        this.awsManager = AwsManager.getAwsManager();
    }

    public void start() {
        this.instanceFactory = new StaticInstanceFactory(awsManager);
    }

    public void stop() {
       instanceFactory.killAllInstances();
    }

    public InetSocketAddress getRandomProxy() {
        return new InetSocketAddress(instanceFactory.getRandomAliveInstance().getIP(), 8888);
    }

    public List<String> getAliveProxies() {
        List<String> res = new ArrayList<>();
        for (LocalInstance instance : instanceFactory.getAliveInstances()) {
            res.add(instance.getIP());
        }
        return res;
    }

    public static void main(String[] args) throws Exception {
        ProxyInstancesManager manager = new ProxyInstancesManager();
        manager.start();
        Thread.sleep(1000);
        manager.stop();
    }
}
