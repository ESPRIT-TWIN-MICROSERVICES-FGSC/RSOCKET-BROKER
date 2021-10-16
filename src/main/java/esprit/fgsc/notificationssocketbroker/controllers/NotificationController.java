package esprit.fgsc.notificationssocketbroker.controllers;

import esprit.fgsc.notificationssocketbroker.models.Notification;
import esprit.fgsc.notificationssocketbroker.repositories.NotificationRepository;
import io.rsocket.core.RSocketServer;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.keepalive.KeepAliveSupport;
import io.rsocket.resume.RSocketSession;
import io.rsocket.resume.SessionManager;
import io.rsocket.util.ByteBufPayload;
import io.rsocket.util.DefaultPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.PayloadUtils;
import org.springframework.messaging.rsocket.RSocketConnectorConfigurer;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.Disposable;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PreDestroy;
import java.io.BufferedWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Slf4j
@Controller
@RestController
@CrossOrigin(origins = "*")
public class NotificationController {
    @Autowired private NotificationRepository notificationRepository;
    public static final Map<String,RSocketRequester> CLIENTS = new HashMap<>();
    // , @Payload String client , @PreAuthorize("hasRole('USER')") , @AuthenticationPrincipal UserDetails user , final FGSCMessage request , @Payload String client
    @ConnectMapping
    @SubscribeMapping
    @RequestMapping
    @MessageMapping
    void connectShellClientAndAskForTelemetry(RSocketRequester requester, @Payload String clientId) {
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
                        Timer timer = new Timer();
                        TimerTask task = new TimerTask() {
                            @Override
                            public void run() {
                                log.debug("scheduling");
                                if (CLIENTS.containsKey(clientId)){
                                    Notification n = new Notification(clientId,"THIS IS A TEST NOTIFICATION");
                                    CLIENTS.get(clientId).route("notifications").data(n).send().subscribe();
                                } else {
                                    cancel();
                                }
                            }
                        };
                        timer.scheduleAtFixedRate(task,new Date(),20000);
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

    @PostMapping("seen")
    public void switchNotificationToSeen(@RequestBody String notificationId){
        this.notificationRepository.findById(notificationId).subscribe(notification -> {
            notification.setSeenAt(Instant.now());
            notificationRepository.save(notification);
        });
    }
    @GetMapping("notifications")
    public Flux<Notification> getAllNotificationsByClient(@RequestParam String clientId, final @RequestParam(name = "page") int page, final @RequestParam(name = "size") int size) {
        return this.notificationRepository.getNotificationByClientIdOrderBySentAtDesc(clientId, PageRequest.of(page, size));
    }
    @GetMapping("notifications/count")
    public Mono<Long> countUnreadNotifications(@RequestParam String clientId) {
        return this.notificationRepository.countByClientIdAndSeenAtNull(clientId);
    }
    @PostMapping
    public Mono<Notification> createNotification( @RequestBody Notification notification){return this.notificationRepository.save(notification);}

    @DeleteMapping("all")
    public Mono<Void> deleteAll(){
        return this.notificationRepository.deleteAll();
    }
    public Mono<Void> deleteByClientId(@RequestParam String clientId){
        return this.notificationRepository.deleteAllByClientId(clientId);
    }

    @MessageExceptionHandler
    public Mono<String> handleException(Throwable t) {
        return Mono.just(t.getMessage());
    }
    @PreDestroy
    void shutdown() {CLIENTS.values().forEach(requester -> Objects.requireNonNull(requester.rsocket()).dispose());}
}
