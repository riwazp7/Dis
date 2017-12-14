package impl.aws;

import api.InstanceFactory;

import java.net.InetSocketAddress;
import java.util.List;

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
        return null;
    }

    public static void main(String[] args) throws Exception {

    }
}
