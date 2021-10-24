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
import org.springframework.http.HttpStatus;
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

import java.time.Instant;
import java.util.*;

@Slf4j
@Controller
@RestController
@CrossOrigin(origins = "*")
public class NotificationController {
    @Autowired
    private NotificationRepository notificationRepository;
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("seen")
    public void switchNotificationToSeen(@RequestBody String notificationId){
        this.notificationRepository.findById(notificationId).subscribe(notification -> {
            notification.setSeenAt(Instant.now());
            notificationRepository.save(notification);
        });
    }
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("notifications")
    public Flux<Notification> getAllNotificationsByClient(@RequestParam String clientId, final @RequestParam(name = "page") int page, final @RequestParam(name = "size") int size) {
        return this.notificationRepository.getNotificationByClientIdOrderBySentAtDesc(clientId, PageRequest.of(page, size));
    }
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("notifications/count")
    public Mono<Long> countUnreadNotifications(@RequestParam String clientId) {
        return this.notificationRepository.countByClientIdAndSeenAtNull(clientId);
    }
    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    public Mono<Notification> createNotification( @RequestBody Notification notification){return this.notificationRepository.save(notification);}
    // TODO : FOR DEV ONLY
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("all")
    public Mono<Void> deleteAll(){
        return this.notificationRepository.deleteAll();
    }
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping
    public Mono<Void> deleteByClientId(@RequestParam String clientId){
        return this.notificationRepository.deleteAllByClientId(clientId);
    }
}
