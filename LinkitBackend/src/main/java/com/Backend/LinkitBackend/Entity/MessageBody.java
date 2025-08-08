package com.Backend.LinkitBackend.Entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;
import java.util.List;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Editor_Rooms")
public class MessageBody {
    @Id
    private String roomId;
    private String content;
    private List<String> participants;
    private List<String> fileNames;

    @CreatedDate
    @Indexed(expireAfterSeconds = 86400) // MongoDB TTL: delete after 1 day
    private Date createdAt;
}
