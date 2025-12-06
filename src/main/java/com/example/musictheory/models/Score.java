package com.example.musictheory.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "scores")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Score {
    @Id
    private ObjectId id;
    private String uid;
    private Integer score;
    private Date date;

}
