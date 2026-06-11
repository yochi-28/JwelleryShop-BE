package org.sharmas.jwelleryshopbe.repository;

import org.sharmas.jwelleryshopbe.models.ReviewModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ReviewRepository extends MongoRepository<ReviewModel, String> {
    List<ReviewModel> findAllByOrderByCreatedAtDesc();
}