package vpn.impl;

public final class LocalInstance {

    private final String instanceId;
    private final String IP;
    private final long killTimeLong;

    public LocalInstance(String instanceId, String IP, long killTimeLong) {
        this.instanceId = instanceId;
        this.IP = IP;
        this.killTimeLong = killTimeLong;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getIP() {
        return IP;
    }

    public long getKillTime() {
        return killTimeLong;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LocalInstance && ((LocalInstance) obj).getInstanceId().equals(getInstanceId());
    }
}