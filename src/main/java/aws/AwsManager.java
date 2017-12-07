package aws;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;

public class AwsManager {

    private static final String instance1 = "i-000e784fdc234bf7d";

    private static AwsManager awsManagerInstance = null;

    public static AwsManager getAwsManager() {
            return (awsManagerInstance == null) ? (awsManagerInstance = new AwsManager()) : awsManagerInstance;
    }

    private final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

    private AwsManager() {}

    public AmazonEC2 getEC2() {
        return ec2;
    }


    public void startInstance(String instance) {
        StartInstancesRequest request = new StartInstancesRequest().withInstanceIds(instance);
        ec2.startInstances(request);
    }

    public void stopInstance(String instance) {
        StopInstancesRequest request = new StopInstancesRequest().withInstanceIds(instance);
        ec2.stopInstances(request);
    }

    public String getInstanceIP(String instance) {
        return "";
//        return ec2.describeInstances(new DescribeInstancesRequest().withInstanceIds(instance))
//                .getReservations().get(0).getInstances().get(0)
//                .getPublicIpAddress();
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

