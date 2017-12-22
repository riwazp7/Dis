package impl.cluster.aws;

import api.InstanceFactory;
import impl.cluster.LocalInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * A manager to start, stop, and query the aws/azure static instance factory.
 */
public class ProxyInstancesManager {

    private static final Logger log = LoggerFactory.getLogger(ProxyInstancesManager.class.getSimpleName());

    private AwsManager awsManager;
    private InstanceFactory instanceFactory;

    public ProxyInstancesManager() {
        this.awsManager = AwsManager.getAwsManager();
    }

    public void start() {
        this.instanceFactory = new StaticInstanceFactory(awsManager);
    }

    public void stop() {
       instanceFactory.killAllInstances();
    }

    public List<String> getAliveProxies() {
        List<String> res = new ArrayList<>();
        for (LocalInstance instance : instanceFactory.getAliveInstances()) {
            res.add(instance.getIP());
        }
        return res;
    }

    public static void main(String[] args) throws Exception {
        ProxyInstancesManager manager = new ProxyInstancesManager();
        manager.start();
        Thread.sleep(1000);
        manager.stop();
    }
}
