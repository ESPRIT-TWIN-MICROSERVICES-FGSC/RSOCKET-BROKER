package esprit.fgsc.notificationssocketbroker.repositories;

import esprit.fgsc.notificationssocketbroker.models.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NotificationRepository extends ReactiveMongoRepository<Notification, String> {
//    @Tailable
    Flux<Notification> getNotificationByClientIdOrderBySentAtDesc(String clientId , final Pageable page);
    Mono<Long> countByClientIdAndSeenAtNull(String clientId);
    Mono<Void> deleteAllByClientId(String clientId);
}
