package vpn.impl;

import aws.AwsManager;
import vpn.api.InstanceFactory;

import java.net.InetSocketAddress;

// **Not thread safe right now
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

    public static void main(String[] args) throws Exception {

    }
}
