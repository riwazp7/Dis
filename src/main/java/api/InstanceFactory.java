package api;

import impl.aws.LocalInstance;

public interface InstanceFactory {
    LocalInstance getRandomAliveInstance();
    void killAllInstances();
}
