package org.sharmas.jwelleryshopbe.repository;

import org.sharmas.jwelleryshopbe.models.ProductModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends MongoRepository<ProductModel, String> {
    Optional<ProductModel> findByName(String username);
    Optional<ProductModel> findById(String id);
    // New method: category match OR name contains the search string (case‑insensitive)
    @Query("{ $or: [ " +
            "{ 'category': { $regex: ?0, $options: 'i' } }, " +
            "{ 'name': { $regex: ?0, $options: 'i' } } " +
            "] }")
    List<ProductModel> findByCategoryOrNameContainingIgnoreCase(String searchTerm);
}
