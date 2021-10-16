package esprit.fgsc.notificationssocketbroker.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.time.Instant;

public class Message{
    @Id @Getter @Setter String id;
    @Getter @Setter Instant sentDate = Instant.now();
    @Getter @Setter String senderId;
    @Getter @Setter String receiverId;
    @Getter @Setter String messageContent;
    @Getter @Setter Instant seenAt;
}
