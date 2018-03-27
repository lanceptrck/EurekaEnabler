package alnaghi.nis.EurekaEnabler.listener;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;

import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.config.DynamicPropertyFactory;

import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.EurekaClient;

import alnaghi.nis.EurekaEnabler.config.EurekaConfig;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class EurekaServletContextListener implements ServletContextListener {
    private DynamicPropertyFactory configInstance;
    private EurekaClient eurekaClient;

 
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        configInstance = com.netflix
                            .config
                            .DynamicPropertyFactory
                            .getInstance();
        ApplicationInfoManager applicationInfoManager =
            EurekaConfig.initializeApplicationInfoManager(new MyDataCenterInstanceConfig());
        eurekaClient = EurekaConfig.initializeEurekaClient(applicationInfoManager, new DefaultEurekaClientConfig());
        start();


    }


    private void start() {
        System.out.println("Registering service to eureka with STARTING status");
        EurekaConfig.getAppInfoManager().setInstanceStatus(InstanceInfo.InstanceStatus.STARTING);

        System.out.println("Simulating service initialization by sleeping for 2 seconds...");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // Nothing
        }

        // Now we change our status to UP
        System.out.println("Done sleeping, now changing status to UP");
        EurekaConfig.getAppInfoManager().setInstanceStatus(InstanceInfo.InstanceStatus.UP);
        waitForRegistrationWithEureka(eurekaClient);
        System.out.println("Service started and ready to process requests..");
        

    }

    private void waitForRegistrationWithEureka(EurekaClient eurekaClient) {
        // my vip address to listen on
        String vipAddress = configInstance.getStringProperty("eureka.vipAddress", "alnaghi.nis.activeuserservice").get();
        InstanceInfo nextServerInfo = null;
        int counter = 0;
        while (nextServerInfo == null && ++counter < 10) {
            try {

                nextServerInfo = eurekaClient.getNextServerFromEureka(vipAddress, false);
                String healthCheckUrl = nextServerInfo.getHealthCheckUrl();
                System.out.println("healthCheckUrl " + healthCheckUrl);
                System.out.println("getMetadata " + nextServerInfo.getMetadata());
                System.out.println("getMetadata ctxRoot " + nextServerInfo.getMetadata().get("ctxRoot"));
                System.out.println("nextServerInfo.getAppName() " + nextServerInfo.getAppName());
            
            } catch (Throwable e) {
                System.out.println("Waiting ... verifying service registration with eureka ...");

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }


    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        if (EurekaConfig.getEurekaClient() != null) {
            EurekaConfig.destroyEurekaClient();
        }
    }
}