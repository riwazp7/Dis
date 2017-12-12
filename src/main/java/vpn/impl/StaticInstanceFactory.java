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
import java.util.concurrent.TimeUnit;

public class StaticInstanceFactory implements InstanceFactory {

    private static final int DEFAULT_NUM_MACHINE = 3;
    private static final int DEFAULT_MIN_LIFESPAN_SECS = 70;
    private static final int DEFAULT_LIFESPAN_VARIANCE_SECS = 120;
    private static final int DEFAULT_START_BUFFER = 60;

    private final Random random = new Random();
    private final Object instancesAccessLock = new Object();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private final List<String> wakingInstances;
    private final List<LocalInstance> aliveInstances;
    private final List<String> sleepingInstances;
    private final List<String> filterInstances; // ?

    private AwsManager awsManager;

    public StaticInstanceFactory(AwsManager awsManager) throws RuntimeException {
        this.awsManager = awsManager;
        this.sleepingInstances = getAllInstances();
        this.wakingInstances = new ArrayList<>();
        this.aliveInstances = new ArrayList<>();
        this.filterInstances = new ArrayList<>();
        init();
    }

    private void init() {
        if (sleepingInstances.size() <= DEFAULT_NUM_MACHINE) {
            throw new RuntimeException("Not enough instances");
        }
        Collections.shuffle(sleepingInstances);
        for (int i = 0; i < DEFAULT_NUM_MACHINE; i++) {
            String instanceId = sleepingInstances.remove(sleepingInstances.size());
            wakeUpInstance(instanceId);
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

                    int lifeSpanSecs = DEFAULT_MIN_LIFESPAN_SECS + random.nextInt(DEFAULT_LIFESPAN_VARIANCE_SECS);
                    int startWakingUpSecs = DEFAULT_START_BUFFER > lifeSpanSecs ?
                            (DEFAULT_START_BUFFER - DEFAULT_MIN_LIFESPAN_SECS) : DEFAULT_START_BUFFER;
                    LocalInstance localInstance
                            = new LocalInstance(instance.getInstanceId(),
                            instance.getPublicIpAddress(),
                            TimeUnit.SECONDS.toMillis(lifeSpanSecs));
                    scheduledExecutorService.schedule(this::wakeUpRandomInstance, startWakingUpSecs, TimeUnit.SECONDS);
                    scheduledExecutorService.schedule(
                            () -> killInstance(localInstance), lifeSpanSecs, TimeUnit.SECONDS);
                    aliveInstances.add(localInstance);
                }
            }
        }
    }

    private void wakeUpRandomInstance() {
        String instanceId;
        synchronized (instancesAccessLock) {
            Collections.shuffle(sleepingInstances);
            instanceId = sleepingInstances.remove(sleepingInstances.size());
            wakingInstances.add(instanceId);
        }
        awsManager.startInstance(instanceId);
    }

    private void killInstance(LocalInstance localInstance) {
        awsManager.stopInstance(localInstance.getInstanceId());
        synchronized (instancesAccessLock) {
            aliveInstances.remove(localInstance);
            sleepingInstances.add(localInstance.getInstanceId());
        }
    }

    private void wakeUpInstance(String instanceId) {
        awsManager.startInstance(instanceId);
        // Needed setUp?
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

    private void addAndStartInstance(String instanceId) {
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
