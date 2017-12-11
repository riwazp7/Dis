package vpn.api;

import vpn.impl.LocalInstance;

public interface InstanceFactory {
    LocalInstance getRandomAliveInstance();
    void killAllInstances();
}
