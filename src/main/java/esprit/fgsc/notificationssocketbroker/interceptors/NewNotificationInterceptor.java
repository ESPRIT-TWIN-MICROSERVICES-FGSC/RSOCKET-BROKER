package esprit.fgsc.notificationssocketbroker.interceptors;

import esprit.fgsc.notificationssocketbroker.controllers.RSocketController;
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
        String clientId = newNotification.getSource().getClientId();
        if(RSocketController.CLIENTS.containsKey(clientId)){
            RSocketController.CLIENTS.get(clientId).route("notifications").data(newNotification.getSource()).send().subscribe();
            log.info("Sent notification to {}",clientId);
        }
    }
}
