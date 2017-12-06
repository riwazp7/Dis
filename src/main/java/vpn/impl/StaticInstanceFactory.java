package vpn.impl;

import aws.AwsManager;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import vpn.api.InstanceFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class StaticInstanceFactory implements InstanceFactory {

    private AwsManager awsManager;

    private static List<String> readStaticInstancesFromFile() {
        return null;
    }

    public StaticInstanceFactory(AwsManager awsManager) {
        this.awsManager = awsManager;
    }

    @Override
    public String getInstance() {
        return getInstance(new HashSet<>());
    }

    @Override
    public String getInstance(Set<String> filter) {
        return getInstances(1, filter).get(0);
    }

    @Override
    public List<String> getInstances(int num) {
        return getInstances(num, new HashSet<>());
    }

    @Override
    public List<String> getInstances(int num, Set<String> filter) {
        List<String> result = new ArrayList<>();
        Iterator<String> stoppedInstances = getStoppedInstances().iterator();
        while(stoppedInstances.hasNext() && num > 0) {
            String instanceId = stoppedInstances.next();
            if (!filter.contains(instanceId)) {
                result.add(instanceId);
            }
        }
        return result;
    }

    private List<String> getStoppedInstances() {
        List<String> stoppedInstances = new ArrayList<>();
        DescribeInstancesResult res = awsManager.getEC2().describeInstances(new DescribeInstancesRequest());
        for (Reservation reservation : res.getReservations()) {
            for (Instance instance : reservation.getInstances()) {
                if ((instance.getState().getCode() & 0xffff) == 16) {
                    stoppedInstances.add(instance.getInstanceId());
                }
            }
        }
        return stoppedInstances;
    }
}
