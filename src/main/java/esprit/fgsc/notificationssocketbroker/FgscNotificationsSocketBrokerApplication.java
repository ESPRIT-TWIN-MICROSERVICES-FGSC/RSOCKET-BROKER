package esprit.fgsc.notificationssocketbroker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.config.EnableWebFlux;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Slf4j
@EnableEurekaClient
@EnableDiscoveryClient
@SpringBootApplication
@EnableSwagger2
@FeignClient(name = "notifs")
@RibbonClient(name = "notifs")
public class FgscNotificationsSocketBrokerApplication {
    public static void main(String[] args) {
        SpringApplication.run(FgscNotificationsSocketBrokerApplication.class, args);
    }
}
