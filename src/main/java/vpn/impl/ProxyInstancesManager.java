package vpn.impl;

import aws.AwsManager;
import vpn.api.InstanceFactory;

import java.util.Random;

// **Not thread safe right now
public class ProxyInstancesManager {

    private AwsManager awsManager;
    private InstanceFactory instanceFactory;
    private Random random;

    public ProxyInstancesManager() {
        this.awsManager = AwsManager.getAwsManager();
        this.random = new Random();
    }

    public void start() throws Exception {
        this.instanceFactory = new StaticInstanceFactory(awsManager);
    }

    public void stop() throws Exception {
       instanceFactory.killAllInstances();
    }

    public static void main(String[] args) throws Exception {
        ProxyInstancesManager manager = new ProxyInstancesManager();
        try {
            manager.start();
            Thread.sleep(200000);
        } finally {
            manager.stop();
        }
    }
}
