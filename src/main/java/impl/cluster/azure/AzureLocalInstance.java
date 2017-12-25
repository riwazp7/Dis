package impl.cluster.azure;

import impl.cluster.LocalInstance;
import impl.proxy.radio.RadioHq;

public class AzureLocalInstance extends LocalInstance {

    public AzureLocalInstance(String awsInstanceId,
                              String IP,
                              RadioHq radioHq,
                              long killTimeLong) {
        super(InstanceProvider.AZURE, instanceIdFromAzureId(awsInstanceId), IP, radioHq, killTimeLong);
    }

    private static String instanceIdFromAzureId(String instanceId) {
        return String.format("AZURE%sAZURE", instanceId);
    }
}
