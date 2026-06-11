package org.sharmas.jwelleryshopbe.repository;

import org.sharmas.jwelleryshopbe.models.PurchaseModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PurchaseRepository extends MongoRepository<PurchaseModel,String> {
    Optional<PurchaseModel> findById(String id);
    List<PurchaseModel> findByUserId(String userId);

}
