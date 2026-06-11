package org.sharmas.jwelleryshopbe.models;

import com.mongodb.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductModel {

    public enum type{
        GOLD,SILVER,PLATINUM,DIAMOND,RUBY
    }

    @Id
    private String id;

    @NonNull
    private type category;

    @NonNull
    @Indexed(unique = true)
    private String name;
    private String description;
    private double gram;
    private double price;
    private String imageUrl;

    @NonNull
    private Integer itemCount;
}
