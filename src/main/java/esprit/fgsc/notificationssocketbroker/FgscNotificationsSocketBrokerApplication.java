package esprit.fgsc.notificationssocketbroker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableEurekaClient
@EnableDiscoveryClient
@SpringBootApplication
public class FgscNotificationsSocketBrokerApplication {
    public static void main(String[] args) {
        SpringApplication.run(FgscNotificationsSocketBrokerApplication.class, args);
    }
}
