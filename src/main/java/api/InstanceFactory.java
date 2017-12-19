package api;

import impl.aws.LocalInstance;

import java.util.List;

public interface InstanceFactory {
    LocalInstance getRandomAliveInstance();
    void killAllInstances();
    List<LocalInstance> getAliveInstances();
}
