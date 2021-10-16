package esprit.fgsc.notificationssocketbroker.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Optional;

@Document
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id @Getter @Setter private String id;
    @Getter @Setter private String clientId;
    @Getter @Setter private Instant sentAt = Instant.now();
    @Getter @Setter private Instant seenAt;
    @Getter @Setter private String message;

    public Notification(String receiverId, String message) {
        this.clientId = receiverId;
        this.message = message;
    }


}
