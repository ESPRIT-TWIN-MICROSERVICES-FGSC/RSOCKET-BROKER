package esprit.fgsc.notificationssocketbroker.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;

public class Message{
    @MongoId(value = FieldType.OBJECT_ID) @Getter @Setter String id;
    @Getter @Setter Instant sentDate = Instant.now();
    @MongoId(value = FieldType.OBJECT_ID) @Getter @Setter String senderId;
    @MongoId(value = FieldType.OBJECT_ID) @Getter @Setter String receiverId;
    @Getter @Setter String messageContent;
    @Getter @Setter Instant seenAt;
    @Getter @Setter String icon;
    @Getter @Setter String url;
}
