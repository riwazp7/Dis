package vpn.impl;

import aws.AwsManager;
import vpn.api.InstanceFactory;

import java.util.concurrent.ConcurrentHashMap;

public class ProxyManager {

    private AwsManager awsManager;
    private ConcurrentHashMap<String, String> instances;
    private InstanceFactory instanceFactory;

    public ProxyManager() {
        this.awsManager = AwsManager.getAwsManager();
        this.instanceFactory = new StaticInstanceFactory(awsManager);
    }

    public void startInstances() {

    }

}
