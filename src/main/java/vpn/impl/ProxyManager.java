package vpn.impl;

import aws.AwsManager;
import vpn.api.InstanceFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// **Not thread safe right now
public class ProxyManager {

    private static final int DEFAULT_NUM_MACHINE = 3;
    private static final int DEFAULT_MIN_LIFESPAN_SECS = 30;
    private static final int DEFAULT_LIFESPAN_VARIANCE_SECS = 70;

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final ConcurrentHashMap<String, String> instances = new ConcurrentHashMap<>();

    private AwsManager awsManager;
    private InstanceFactory instanceFactory;
    private Random random;

    public ProxyManager() {
        this.awsManager = AwsManager.getAwsManager();
        this.instanceFactory = new StaticInstanceFactory(awsManager);
        this.random = new Random();
    }

    public void start() throws Exception {
        System.out.println("START******");
        List<String> ins = instanceFactory.getInstances(DEFAULT_NUM_MACHINE);
        System.out.println(ins);
        for (String instanceId : ins) {
            addAndStartInstance(instanceId);
        }
    }

    public void stop() throws Exception {
        for (String instanceId : instances.keySet()) {
            awsManager.stopInstance(instanceId);
        }
    }

    private void killAndReplace(String instanceId) {
        awsManager.stopInstance(instanceId);
        System.out.println("Removed instance: " + instanceId);
        String newInstanceId = instanceFactory.getInstance(new HashSet<>(instances.keySet()));
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
        ProxyManager manager = new ProxyManager();
        try {
            manager.start();
            Thread.sleep(200000);
        } finally {
            manager.stop();
        }
    }
}
