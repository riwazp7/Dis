package impl.cluster;

import impl.proxy.radio.RadioHq;

/**
 * A local representation of an aws instance. Has the instance's ID, current IP, time before it dies, and
 * the gRPC client instance to talk to it.
 */
public abstract class LocalInstance {

    public enum InstanceProvider {
        AWS, AZURE
    }

    private final InstanceProvider instanceProvider;
    private final String instanceId;
    private final String IP;
    private final long killTimeLong;
    private final RadioHq radioHq;

    public LocalInstance(
            InstanceProvider instanceProvider,
            String instanceId,
            String IP,
            RadioHq radioHq,
            long killTimeLong) {
        this.instanceProvider = instanceProvider;
        this.instanceId = instanceId;
        this.IP = IP;
        this.killTimeLong = killTimeLong;
        this.radioHq = radioHq;
    }

    public InstanceProvider getInstanceProvider() {
        return instanceProvider;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getIP() {
        return IP;
    }

    // doesn't work as intended
    public long getKillTime() {
        return killTimeLong;
    }

    public RadioHq getRadioHq() {
        return radioHq;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LocalInstance && ((LocalInstance) obj).getInstanceId().equals(getInstanceId());
    }
}