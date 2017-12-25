package impl.cluster.aws;

import impl.cluster.LocalInstance;
import impl.proxy.radio.RadioHq;

public class AwsLocalInstance extends LocalInstance {

    public AwsLocalInstance(String awsInstanceId,
                            String IP,
                            RadioHq radioHq,
                            long killTimeLong) {
        super(InstanceProvider.AWS, instanceIdFromAwsId(awsInstanceId), IP, radioHq, killTimeLong);
    }

    private static String instanceIdFromAwsId(String instanceId) {
        return String.format("AWS%sAWS", instanceId);
    }
}
