package impl.aws;

import impl.proxy.radio.RadioHq;

/**
 * A local representation of an aws instance. Has the instance's ID, current IP, time before it dies, and
 * the gRPC client instance to talk to it.
 */
public final class LocalInstance {

    private final String instanceId;
    private final String IP;
    private final long killTimeLong;
    private final RadioHq radioHq;

    public LocalInstance(
            String instanceId,
            String IP,
            RadioHq radioHq,
            long killTimeLong) {
        this.instanceId = instanceId;
        this.IP = IP;
        this.killTimeLong = killTimeLong;
        this.radioHq = radioHq;
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