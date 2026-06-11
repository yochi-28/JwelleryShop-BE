package org.sharmas.jwelleryshopbe.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Document(collection = "pricing")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PricingModel {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;
    private double CurrentPricingPerGram;

    @CreatedDate
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime lastUpdated;
}
