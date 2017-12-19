package impl.aws;

import api.InstanceFactory;
import com.amazonaws.services.ec2.model.Instance;
import impl.proxy.local.ClientManager;
import impl.proxy.radio.RadioHq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

/**
 * StaticInstanceFactory.java
 * Manages starting, running for a random time, and stopping aws ec2 instances for our use.
 */
public class StaticInstanceFactory implements InstanceFactory {

    /**
     * The number of proxies alive at a time.
     */
    private static final int DEFAULT_NUM_MACHINE = 3;

    /**
     * The minimum lifespan of each proxy machine.
     */
    private static final int DEFAULT_MIN_LIFESPAN_SECS = 120;

    /**
     * Random variance in minimum machine lifespan.
     */
    private static final int DEFAULT_LIFESPAN_VARIANCE_SECS = 60;

    /**
     * When to initiate starting an aws machine.
     */
    private static final int DEFAULT_START_BUFFER = 20;

    private final Random random = new Random();
    private final Object instancesAccessLock = new Object();
    private final ScheduledExecutorService scheduledExecutorService;

    private final List<String> wakingInstances;
    private final List<LocalInstance> aliveInstances;
    private final List<String> sleepingInstances;

    private AwsManager awsManager;

    public StaticInstanceFactory(AwsManager awsManager) throws RuntimeException {
        this.awsManager = awsManager;
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        this.sleepingInstances = getAllInstances();
        this.wakingInstances = new ArrayList<>();
        this.aliveInstances = new ArrayList<>();
        init();
    }

    private void init() {
        if (sleepingInstances.size() <= DEFAULT_NUM_MACHINE) {
            throw new RuntimeException("Not enough instances");
        }
        Collections.shuffle(sleepingInstances);
        for (int i = 0; i < DEFAULT_NUM_MACHINE; i++) {
            System.out.println("started 1");
            String instanceId = sleepingInstances.remove(sleepingInstances.size() - 1);
            wakeUpInstance(instanceId);
            wakingInstances.add(instanceId);
        }
        awaitAllWakingInstances();
    }

    private void awaitAllWakingInstances() {
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
                    registerTimedLocalInstance(instance);
                }
            }
        }
    }

    private void awaitSingleWakingInstance() {
        synchronized (instancesAccessLock) {
            if (wakingInstances.isEmpty()) {
                throw new RuntimeException("waking instance list empty");
            }
            List<Instance> instances = awsManager.getInstanceDescription(wakingInstances);
            while (true) { // Bad Idea
                for (Instance instance : instances) {
                    if ((instance.getState().getCode() & 0xff) == 16) {
                        wakingInstances.remove(instance.getInstanceId());
                        registerTimedLocalInstance(instance);
                        return;
                    }
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    //
                }
            }
        }
    }

    private void registerTimedLocalInstance(Instance instance) {
        int lifeSpanSecs = DEFAULT_MIN_LIFESPAN_SECS + random.nextInt(DEFAULT_LIFESPAN_VARIANCE_SECS);
        LocalInstance localInstance
                = new LocalInstance(instance.getInstanceId(),
                instance.getPublicIpAddress(),
                new RadioHq(instance.getPublicIpAddress(), ClientManager.DEF_HQ_PORT),
                TimeUnit.SECONDS.toMillis(lifeSpanSecs));
        scheduledExecutorService.schedule(
                this::wakeUpRandomInstance, lifeSpanSecs - DEFAULT_START_BUFFER, TimeUnit.SECONDS);
        scheduledExecutorService.schedule(
                () -> killInstance(localInstance), lifeSpanSecs, TimeUnit.SECONDS);
        aliveInstances.add(localInstance);
    }

    private void wakeUpRandomInstance() {
        String instanceId;
        synchronized (instancesAccessLock) {
            Collections.shuffle(sleepingInstances);
            instanceId = sleepingInstances.remove(sleepingInstances.size() - 1);
            wakingInstances.add(instanceId);
        }
        awsManager.startInstance(instanceId);
    }

    private void killInstance(LocalInstance localInstance) {
        awaitSingleWakingInstance();
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

    @Override
    public List<LocalInstance> getAliveInstances() {
        synchronized (instancesAccessLock) {
            return Collections.unmodifiableList(aliveInstances);
        }
    }

    @Override
    @Nullable
    public LocalInstance getRandomAliveInstance() {
        // Check instance is alive (impl.proxy.radio)
        // Check time
        synchronized (instancesAccessLock) {
            if (aliveInstances.isEmpty()) {
                throw new RuntimeException("No instances alive");
            }
            return aliveInstances.get(random.nextInt(aliveInstances.size()));
        }
    }

    public List<String> getAllInstances() {
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

    public static void main(String[] args) {
        // Test
        StaticInstanceFactory factory = null;
        try {
            factory = new StaticInstanceFactory(AwsManager.getAwsManager());
            for (int i = 0; i < 100; i++) {
                System.out.println("Random Ip: " + factory.getRandomAliveInstance().getIP());
                Thread.sleep(5000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (factory != null)
                factory.killAllInstances();
        }
    }
}
