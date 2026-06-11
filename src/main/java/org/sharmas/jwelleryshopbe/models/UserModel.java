package org.sharmas.jwelleryshopbe.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {
    public enum Role{
        ADMIN,USER
    }

    @Id
    private String id;

    @NotBlank(message="Name cannot be empty")
    @Indexed(unique = true)
    private String username;

    @NotBlank(message="Password can't be empty")
    private String password;

    @CreatedDate
    private Date creationDate;

    @Email(message = "Must be a valid email format")
    @NotBlank(message = "Email cannot be empty")
    private String email;

    private Role role = Role.USER;

    @DBRef(lazy = true)
    private List<ProductModel> products = new ArrayList<>();

    @DBRef(lazy = true)
    private List<PurchaseModel> purchaseHistory = new ArrayList<>();
}
