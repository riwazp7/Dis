package vpn.impl;

import aws.AwsManager;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import vpn.api.InstanceFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class StaticInstanceFactory implements InstanceFactory {

    private List<String> staticInstanceList;
    private AwsManager awsManager;

    public StaticInstanceFactory(AwsManager awsManager) {
        this.awsManager = awsManager;
        this.staticInstanceList = getAllInstances(awsManager);
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
        Collections.shuffle(staticInstanceList);
        List<String> result = new ArrayList<>();
        for (String id : staticInstanceList) {
            if (result.size() >= num) {
                break;
            }
            if (!filter.contains(id)) {
                result.add(id);
            }
        }
        return result;
    }

    private static List<String> getAllInstances(AwsManager awsManager) {
        List<String> instances = new LinkedList<>();
        DescribeInstancesResult res = awsManager.getEC2().describeInstances(new DescribeInstancesRequest());
        for (Reservation reservation : res.getReservations()) {
            for (Instance instance : reservation.getInstances()) {
                instances.add(instance.getInstanceId());
            }
        }
        return instances;
    }
}
