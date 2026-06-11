package org.sharmas.jwelleryshopbe.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.sharmas.jwelleryshopbe.models.PricingModel;
import org.sharmas.jwelleryshopbe.repository.PricingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Configuration
public class PricingSeeder {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GoldApiResponse(double price_gram_24k) {
    }

    @Value("${goldapi.key}")
    private String goldApiKey;

    @Bean
    CommandLineRunner initPricingDb(PricingRepository pricingRepository, RestTemplate restTemplate) {
        return args -> {

            System.out.println("⏳ Checking Pricing Database...");

            // 1. Hardcoded Gemstones (Per Carat)
            savePricingIfMissing(pricingRepository, "DIAMOND", 76000.0);
            savePricingIfMissing(pricingRepository, "RUBY", 1000000.0);

            // 2. Fetch Precious Metals from API (Currency set to INR)
            fetchAndSaveMetal(pricingRepository, restTemplate, "GOLD", "XAU");
            fetchAndSaveMetal(pricingRepository, restTemplate, "SILVER", "XAG");
            fetchAndSaveMetal(pricingRepository, restTemplate, "PLATINUM", "XPT");

            System.out.println("Pricing Database is up to date!");
        };
    }

    // Helper method for Gemstones
    private void savePricingIfMissing(PricingRepository repo, String name, double price) {
        if (repo.findByName(name).isEmpty()) {
            PricingModel newPricing = new PricingModel();
            newPricing.setName(name);
            newPricing.setCurrentPricingPerGram(price); // Passing your double directly!

            repo.save(newPricing);
            System.out.println("💎 Saved default price for: " + name);
        }
    }

    // Helper method to call GoldAPI
    private void fetchAndSaveMetal(PricingRepository repo, RestTemplate restTemplate, String name, String symbol) {
        if (repo.findByName(name).isEmpty()) {
            try {
                String url = "https://www.goldapi.io/api/" + symbol + "/INR";
                HttpHeaders headers = new HttpHeaders();
                headers.set("x-access-token", goldApiKey);
                HttpEntity<String> entity = new HttpEntity<>(headers);

                ResponseEntity<GoldApiResponse> response = restTemplate.exchange(
                        url, HttpMethod.GET, entity, GoldApiResponse.class
                );

                if (response.getBody() != null) {
                    double price = response.getBody().price_gram_24k();

                    PricingModel livePricing = new PricingModel();
                    livePricing.setName(name);
                    livePricing.setCurrentPricingPerGram(price); // Passing your double directly!

                    repo.save(livePricing);
                    System.out.println("Fetched live price for " + name + ": ₹" + price + "/gram");
                }
            } catch (Exception e) {
                System.err.println("Could not fetch price for " + name + ". Using fallback. Error: " + e.getMessage());
                double fallbackPrice = name.equals("GOLD") ? 7100.0 : name.equals("SILVER") ? 85.0 : 2500.0;

                PricingModel fallbackModel = new PricingModel();
                fallbackModel.setName(name);
                fallbackModel.setCurrentPricingPerGram(fallbackPrice);

                repo.save(fallbackModel);
            }
        }
    }
}