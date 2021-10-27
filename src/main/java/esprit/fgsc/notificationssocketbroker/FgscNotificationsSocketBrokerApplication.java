package esprit.fgsc.notificationssocketbroker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@Slf4j
@EnableEurekaClient
@EnableDiscoveryClient
@SpringBootApplication
public class FgscNotificationsSocketBrokerApplication {
    public static void main(String[] args) {
        SpringApplication.run(FgscNotificationsSocketBrokerApplication.class, args);
    }
}
