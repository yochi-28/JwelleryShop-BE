package org.sharmas.jwelleryshopbe.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection="purchase")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseModel {
    public enum Status{
        PENDING, PAID, SHIPPED, DELIVERED
    }

    @Id
    private String id;

    private String userId;

    private List<PurchaseItem> items = new ArrayList<>();

    private double totalAmount;

    private Status orderStatus = Status.PENDING;

    @CreatedDate
    private Date purchaseDate;

    @Data
    public static class PurchaseItem {
        private String productId;
        private int itemCount;
        private double priceAtPurchase;
    }
}
