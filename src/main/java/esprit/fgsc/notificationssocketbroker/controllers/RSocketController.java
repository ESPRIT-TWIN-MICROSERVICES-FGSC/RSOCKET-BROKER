package esprit.fgsc.notificationssocketbroker.controllers;

import esprit.fgsc.notificationssocketbroker.models.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;

import javax.annotation.PreDestroy;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/rs")
@CrossOrigin("*")
public class RSocketController {

    public static Map<String, RSocketRequester> CLIENTS = new HashMap<>();
    // , @Payload String client , @PreAuthorize("hasRole('USER')") , @AuthenticationPrincipal UserDetails user , final FGSCMessage request , @Payload String client
    @ConnectMapping
    @SubscribeMapping
    @RequestMapping
    @MessageMapping
    void connectShellClientAndAskForTelemetry(RSocketRequester requester, @Payload String clientId) {
        System.out.println("ok");
        log.info("OK");
        Hooks.onErrorDropped((aze) -> {
            log.debug("Dropped client : {}",clientId);
        });
        try {
            Objects.requireNonNull(requester.rsocket()).onClose().doFinally(terminalSignal -> {
                log.info("closed, signal : {}",terminalSignal);
                CLIENTS.remove(clientId);
                requester.dispose();
            }).subscribe();
            Objects.requireNonNull(requester.rsocket())
                    .onClose()
                    .doFirst(() -> {
                        CLIENTS.put(clientId,requester);
                        log.info("Client : {} connected", clientId);
                    })
                    .doOnError(e -> {
                        log.warn("Error with socket",e);
                        CLIENTS.remove(clientId);
                        requester.dispose();
                    })
                    .doFinally(consumer -> {
                        log.debug("Removing client {}",clientId);
                        CLIENTS.remove(clientId);
                        requester.dispose();
                    })
                    .subscribe();
        } catch (Exception e){
            log.warn("Exception : {}",e.getMessage());
            CLIENTS.remove(clientId);
            requester.dispose();
        }
    }


    @MessageExceptionHandler
    public Mono<String> handleException(Throwable t) {
        return Mono.just(t.getMessage());
    }
    @PreDestroy
    void shutdown() {CLIENTS.values().forEach(requester -> Objects.requireNonNull(requester.rsocket()).dispose());}
}
