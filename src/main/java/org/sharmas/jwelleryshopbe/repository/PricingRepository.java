package org.sharmas.jwelleryshopbe.repository;

import org.sharmas.jwelleryshopbe.models.PricingModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PricingRepository extends MongoRepository<PricingModel, String> {
    Optional<PricingModel> findByName(String username);
}
