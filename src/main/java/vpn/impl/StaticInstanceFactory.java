package vpn.impl;

import aws.AwsManager;
import com.amazonaws.services.ec2.model.Instance;
import jdk.internal.jline.internal.Nullable;
import vpn.api.InstanceFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class StaticInstanceFactory implements InstanceFactory {

    private static final int DEFAULT_NUM_MACHINE = 3;
    private static final int DEFAULT_MIN_LIFESPAN_SECS = 30;
    private static final int DEFAULT_LIFESPAN_VARIANCE_SECS = 70;

    private final Random random = new Random();
    private final Object instancesAccessLock = new Object();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private List<String> wakingInstances;
    private List<LocalInstance> aliveInstances;
    private List<String> sleepingInstances;

    private AwsManager awsManager;

    public StaticInstanceFactory(AwsManager awsManager) throws RuntimeException {
        this.awsManager = awsManager;
        this.sleepingInstances = getAllInstances();
        init();
    }

    private void init() {
        if (sleepingInstances.size() <= DEFAULT_NUM_MACHINE) {
            throw new RuntimeException("Not enough instances");
        }
        Collections.shuffle(sleepingInstances);
        for (int i = 0; i < DEFAULT_NUM_MACHINE; i++) {
            String instanceId = sleepingInstances.remove(sleepingInstances.size());
            wakeUp(instanceId);
            wakingInstances.add(instanceId);
        }
        awaitWakingInstances();
    }

    // Also check other statuses?
    private void awaitWakingInstances() {
        while (wakingInstances.size() > 0) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // do nth
            }
            List<Instance> instances = awsManager.getInstanceDescription(wakingInstances);
            for (Instance instance : instances) {
                if ((instance.getState().getCode() & 0xff) == 16) {
                    wakingInstances.remove(instance.getInstanceId());
                    LocalInstance localInstance
                            = new LocalInstance(instance.getInstanceId(), instance.getPublicIpAddress(), 0);
                    aliveInstances.add(localInstance);
                }
            }
        }
    }

    private void wakeUp(String instanceId) {
        awsManager.startInstance(instanceId);
        // Needed setUp?
    }

    private void wakeUpInstance(String instanceId) {

    }

    public List<LocalInstance> getAliveInstances() {
        synchronized (instancesAccessLock) {
            return Collections.unmodifiableList(aliveInstances);
        }
    }

    @Override
    @Nullable
    public LocalInstance getRandomAliveInstance() {
        // Check instance is alive (radio)
        // Check time
        synchronized (instancesAccessLock) {
            return aliveInstances.get(random.nextInt(aliveInstances.size()));
        }
    }

    private List<String> getAllInstances() {
        List<String> result = new ArrayList<>();
        for (Instance instance : awsManager.getAllInstanceDescription()) {
            result.add(instance.getInstanceId());
        }
        return result;
    }

    public void killAllInstances() {
        synchronized (instancesAccessLock) {
            for (LocalInstance instance : aliveInstances) {
                awsManager.stopInstance(instance.getInstanceId());
            }
        }
    }
}
