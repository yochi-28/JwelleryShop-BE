package org.sharmas.jwelleryshopbe.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewModel {
    @Id
    private String id;
    private int rating;
    private String comment;
    private String userId;
    private String username;
    private Date createdAt;
}