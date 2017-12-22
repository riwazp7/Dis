package api;

import impl.cluster.LocalInstance;

import java.util.List;

public interface InstanceFactory {
    LocalInstance getRandomAliveInstance();
    void killAllInstances();
    List<LocalInstance> getAliveInstances();
}
