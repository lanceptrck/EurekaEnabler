package alnaghi.nis.EurekaEnabler.healthcheck;

import com.netflix.appinfo.HealthCheckHandler;
import com.netflix.appinfo.InstanceInfo;

public class EurekaHealthCheckHandler implements HealthCheckHandler {
    public EurekaHealthCheckHandler() {
        super();
    }

    public InstanceInfo.InstanceStatus getStatus(InstanceInfo.InstanceStatus instanceStatus) {
        return InstanceInfo.InstanceStatus.UP;
    }
}