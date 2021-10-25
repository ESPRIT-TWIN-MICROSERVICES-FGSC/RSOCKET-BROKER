package esprit.fgsc.notificationssocketbroker.controllers;

import esprit.fgsc.notificationssocketbroker.models.Notification;
import esprit.fgsc.notificationssocketbroker.repositories.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Slf4j
@CrossOrigin("*")
@RestController
public class NotificationController {
    @Autowired
    private NotificationRepository notificationRepository;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("seen")
    public void switchNotificationToSeen(@RequestBody String notificationId) {
        this.notificationRepository.findById(notificationId).subscribe(notification -> {
            notification.setSeenAt(Instant.now());
            notificationRepository.save(notification);
        });
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Flux<Notification> getAllNotificationsByClient(@RequestParam String clientId, final @RequestParam(name = "page") int page, final @RequestParam(name = "size") int size) {
        return this.notificationRepository.getNotificationByClientIdOrderBySentAtDesc(clientId, PageRequest.of(page, size));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/count")
    public Mono<Long> countUnreadNotifications(@RequestParam String clientId) {
        return this.notificationRepository.countByClientIdAndSeenAtNull(clientId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    public Mono<Notification> createNotification(@RequestBody Notification notification) {
        return this.notificationRepository.save(notification);
    }

    // TODO : FOR DEV ONLY
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("all")
    public Mono<Void> deleteAll() {
        return this.notificationRepository.deleteAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping
    public Mono<Void> deleteByClientId(@RequestParam String clientId) {
        return this.notificationRepository.deleteAllByClientId(clientId);
    }
}
