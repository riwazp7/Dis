package impl.aws;

import api.InstanceFactory;

import java.net.InetSocketAddress;
import java.util.List;

public class ProxyInstancesManager {

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

    public InetSocketAddress getRandomProxy() {
        return new InetSocketAddress(instanceFactory.getRandomAliveInstance().getIP(), 8888);
    }

    // Improvements:
    // Do not sent the IP of machines whose remaining lifespan is shorter than the lifespan to the connection.
    public List<String> getAliveProxies() {
        return instanceFactory.getAllInstances();
    }

    public static void main(String[] args) throws Exception {

    }
}
