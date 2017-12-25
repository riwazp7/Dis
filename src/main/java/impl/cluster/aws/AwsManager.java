package impl.cluster.aws;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * AwsManager.java
 * Handles ec2 start, stop, and description query commands through the aws api.
 */
public class AwsManager {

    private final Logger log = LoggerFactory.getLogger(AwsManager.class.getSimpleName());

    private static AwsManager awsManagerInstance;

    public static AwsManager getAwsManager() {
            return (awsManagerInstance == null) ? (awsManagerInstance = new AwsManager()) : awsManagerInstance;
    }

    private final AmazonEC2 ec2;

    private AwsManager() {
        this.ec2 = AmazonEC2ClientBuilder.defaultClient();
    }

    public void startInstance(String instance) {
        StartInstancesRequest request = new StartInstancesRequest().withInstanceIds(instance);
        ec2.startInstances(request);
    }

    public void stopInstance(String instance) {
        StopInstancesRequest request = new StopInstancesRequest().withInstanceIds(instance);
        ec2.stopInstances(request);
    }

    public List<Instance> getInstanceDescription(List<String> instanceId) {
        List<Instance> instances = new ArrayList<>();
        DescribeInstancesResult result = ec2.describeInstances(
                new DescribeInstancesRequest().withInstanceIds(instanceId));
        for (Reservation reservation : result.getReservations()) {
            instances.addAll(reservation.getInstances());
        }
        return instances;
    }

    public List<Instance> getAllInstanceDescription() {
        List<Instance> instances = new ArrayList<>();
        for (Reservation reservation : ec2.describeInstances(new DescribeInstancesRequest()).getReservations()) {
            instances.addAll(reservation.getInstances());
        }
        return instances;
    }

    public static void main(String[] args) throws Exception {

    }
}

