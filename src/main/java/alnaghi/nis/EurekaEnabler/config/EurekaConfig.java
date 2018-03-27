package alnaghi.nis.EurekaEnabler.config;

import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.providers.EurekaConfigBasedInstanceInfoProvider;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.EurekaClientConfig;

public class EurekaConfig {

    private static ApplicationInfoManager applicationInfoManager;
    private static EurekaClient eurekaClient;
    
    public static synchronized ApplicationInfoManager initializeApplicationInfoManager(EurekaInstanceConfig instanceConfig) {
          if (applicationInfoManager == null) {
              InstanceInfo instanceInfo = new EurekaConfigBasedInstanceInfoProvider(instanceConfig).get();
              applicationInfoManager = new ApplicationInfoManager(instanceConfig, instanceInfo);
          }

          return applicationInfoManager;
      }

      public static synchronized EurekaClient initializeEurekaClient(ApplicationInfoManager applicationInfoManager, EurekaClientConfig clientConfig) {
          if (eurekaClient == null) {
              eurekaClient = new DiscoveryClient(applicationInfoManager, clientConfig);
          }

          return eurekaClient;
      }

      public static synchronized ApplicationInfoManager getAppInfoManager() {
          return applicationInfoManager;
      }
      
      
    public static EurekaClient getEurekaClient()
    {
        ApplicationInfoManager applicationInfoManager = initializeApplicationInfoManager(new MyDataCenterInstanceConfig());
        EurekaClient eurekaClient = initializeEurekaClient(applicationInfoManager, new DefaultEurekaClientConfig());
        return eurekaClient;
    }
    
    public static void destroyEurekaClient() {
        if (eurekaClient != null) {
            System.out.println("Shutting down server. Demo over.");
            eurekaClient.shutdown();
            eurekaClient = null;
        }
    }
}