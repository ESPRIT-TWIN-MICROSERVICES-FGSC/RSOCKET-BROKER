package esprit.fgsc.notificationssocketbroker.interceptors;

import esprit.fgsc.notificationssocketbroker.controllers.NotificationController;
import esprit.fgsc.notificationssocketbroker.models.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
@Slf4j
@Configuration
public class NewNotificationInterceptor extends AbstractMongoEventListener<Notification> {
    @Override
    public void onAfterSave(AfterSaveEvent<Notification> newNotification) {
        super.onAfterSave(newNotification);
        log.debug("Sending from interceptor");
        String clientId = newNotification.getSource().getClientId().toString();
        if(NotificationController.CLIENTS.containsKey(clientId)){
            NotificationController.CLIENTS.get(clientId).route("notifications").data(newNotification.getSource()).send();
            log.debug("Send notification from interceptor to client : {}",clientId);
        }
    }
}
