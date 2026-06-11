package org.sharmas.jwelleryshopbe.controller;

import org.sharmas.jwelleryshopbe.models.PricingModel;
import org.sharmas.jwelleryshopbe.repository.PricingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PriceController {
    private PricingRepository pricingRepository;
    public PriceController(PricingRepository pricingRepository){
        this.pricingRepository = pricingRepository;
    }

    @GetMapping("/jwel/prices")
    public ResponseEntity<List<PricingModel>> getJwelPrices(){
        try {
            List<PricingModel> allPrices = pricingRepository.findAll();
            return ResponseEntity.ok(allPrices);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
