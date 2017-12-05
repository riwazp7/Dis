package aws;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AwsManager {

    private static final String instance1 = "i-000e784fdc234bf7d";

    private static AwsManager awsManagerInstance = null;

    static AwsManager getAwsManager() {
            return (awsManagerInstance == null) ? (awsManagerInstance = new AwsManager()) : awsManagerInstance;
    }

    private final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
    private final ConcurrentHashMap<String, String> instancetoIPMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();


    private AwsManager() {}

    private void startInstance(String instance) {
        StartInstancesRequest request = new StartInstancesRequest().withInstanceIds(instance);
        ec2.startInstances(request);
        instancetoIPMap.put(instance, getInstanceIP(instance));
    }

    private void stopInstance(String instance) {
        StopInstancesRequest request = new StopInstancesRequest().withInstanceIds(instance);
        ec2.stopInstances(request);
    }

    private void scheduleStop(String instance) {
        scheduledExecutorService.schedule(() -> {
            instancetoIPMap.remove(instance);
            stopInstance(instance);
        }, 50, TimeUnit.SECONDS);
    }

    private String getInstanceIP(String instance) {
        return ec2.describeInstances(new DescribeInstancesRequest().withInstanceIds(instance))
                .getReservations().get(0).getInstances().get(0)
                .getPublicIpAddress();
    }

    public static void main(String[] args) throws Exception {
        AwsManager manager = AwsManager.getAwsManager();
        try {
            manager.startInstance(instance1);
            System.out.println("Start request sent. Waiting for 100 secs.");
            Thread.sleep(100000);
        } finally {
            manager.stopInstance(instance1);
            System.out.println("Stop request sent.");
        }
    }
}

