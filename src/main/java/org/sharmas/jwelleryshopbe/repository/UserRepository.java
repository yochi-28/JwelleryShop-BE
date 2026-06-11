package org.sharmas.jwelleryshopbe.repository;

import org.sharmas.jwelleryshopbe.models.UserModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<UserModel, String> {
        // Spring Data automatically writes the MongoDB query for this!
        Optional<UserModel> findById(String id);
        Optional<UserModel> findByUsername(String username);
}
