package impl.aws;

import impl.proxy.radio.RadioHq;

import javax.annotation.Nullable;

public final class LocalInstance {

    private final String instanceId;
    private final String IP;
    private final long killTimeLong;
    @Nullable
    private RadioHq radioHq;

    public LocalInstance(String instanceId, String IP, long killTimeLong) {
        this.instanceId = instanceId;
        this.IP = IP;
        this.killTimeLong = killTimeLong;
    }

    public void setRadioHq(@Nullable  RadioHq radioHq) {
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

    @Nullable
    public RadioHq getRadioHq() {
        return radioHq;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LocalInstance && ((LocalInstance) obj).getInstanceId().equals(getInstanceId());
    }
}