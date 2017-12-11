package vpn.impl;

import aws.AwsManager;
import vpn.api.InstanceFactory;

import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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

    private void killAndReplace(String instanceId) {
        awsManager.stopInstance(instanceId);
        System.out.println("Removed instance: " + instanceId);
        String newInstanceId = instanceFactory.getInstance(new HashSet<>(instances));
        instances.remove(instanceId); // request before removing.
        System.out.println("Starting instance: " + newInstanceId);
        addAndStartInstance(newInstanceId);
    }

    private void addAndStartInstance(String instanceId) {
        awsManager.startInstance(instanceId);
        instances.put(instanceId, awsManager.getInstanceIP(instanceId));
        scheduledExecutorService.schedule(
                () -> killAndReplace(instanceId),
                DEFAULT_MIN_LIFESPAN_SECS + random.nextInt(DEFAULT_LIFESPAN_VARIANCE_SECS),
                TimeUnit.SECONDS);
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
