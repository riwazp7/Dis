package vpn.api;

import java.util.List;
import java.util.Set;

public interface InstanceFactory {
    String getInstance();
    String getInstance(Set<String> filter);
    List<String> getInstances(int num);
    List<String> getInstances(int num, Set<String> filter);
}
