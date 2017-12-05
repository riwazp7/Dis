package imp;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.StartInstancesRequest;

public class AwsManager {
    private static final String instance = "i-000e784fdc234bf7d";

    public static void main(String[] args) {
        AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
        StartInstancesRequest request = new StartInstancesRequest().withInstanceIds(instance);
        ec2.startInstances(request);
    }

}
