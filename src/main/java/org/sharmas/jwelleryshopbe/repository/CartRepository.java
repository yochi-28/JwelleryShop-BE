package org.sharmas.jwelleryshopbe.repository;

import org.sharmas.jwelleryshopbe.models.CartModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface CartRepository extends MongoRepository<CartModel, String> {
    Optional<CartModel> findByUserId(String userId);
}