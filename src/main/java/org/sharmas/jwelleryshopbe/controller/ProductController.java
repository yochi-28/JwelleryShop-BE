package org.sharmas.jwelleryshopbe.controller;

import org.sharmas.jwelleryshopbe.models.ProductModel;
import org.sharmas.jwelleryshopbe.models.UserModel;
import org.sharmas.jwelleryshopbe.repository.PricingRepository;
import org.sharmas.jwelleryshopbe.repository.ProductRepository;
import org.sharmas.jwelleryshopbe.repository.UserRepository;
import org.sharmas.jwelleryshopbe.services.CloudinaryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class ProductController {

    private final CloudinaryService cloudinaryService;
    private final ProductRepository productRepository;
    private final PricingRepository pricingRepository;
    private final UserRepository userRepository;

    public ProductController(CloudinaryService cloudinaryService, ProductRepository productRepository, PricingRepository pricingRepository, UserRepository userRepository) {
        this.cloudinaryService = cloudinaryService;
        this.productRepository = productRepository;
        this.pricingRepository = pricingRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public ResponseEntity<Page<ProductModel>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size){

        // Creates a request for a specific "page" with a specific number of items
        Pageable paging = PageRequest.of(page, size);
        return ResponseEntity.ok(productRepository.findAll(paging)
        );
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<?> findProductByName(@PathVariable String id){

        return productRepository.findById(id)
                .map(product -> ResponseEntity.ok(product))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/products/category/{category}")
    public ResponseEntity<?> findProductsByCategory(@PathVariable String category) {
        // Use the new repository method that searches both category and name
        List<ProductModel> products = productRepository.findByCategoryOrNameContainingIgnoreCase(category);
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(products);
    }

    @PostMapping("/product")
    public ResponseEntity<?> createProduct(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("category") ProductModel.type category,
            @RequestParam("description") String description,
            @RequestParam("gram") long gram,
            @RequestParam("itemCount") Integer itemCount) {

        try {
            String imageUrl = cloudinaryService.uploadImage(file);

            ProductModel newProduct = new ProductModel();
            newProduct.setName(name);
            newProduct.setCategory(category);
            newProduct.setDescription(description);
            newProduct.setGram(gram);
            newProduct.setItemCount(itemCount);
            newProduct.setImageUrl(imageUrl);

            double perGramPrice = pricingRepository.findByName(category.name())
                    .orElseThrow(() -> new RuntimeException("Wait! Pricing data not found for: " + name))
                    .getCurrentPricingPerGram();
            double makingCharges = 1000;
            double price = (gram * perGramPrice) + makingCharges;
            newProduct.setPrice(price);

            ProductModel savedProduct = productRepository.save(newProduct);

            return ResponseEntity.ok(savedProduct);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/product/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable("id") String id, // FIXED: Now accepts MongoDB String IDs
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "category", required = false) ProductModel.type category,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "gram", required = false) Double gram, // FIXED: Double prevents crash if empty
            @RequestParam(value = "itemCount", required = false) Integer itemCount) {

        try {
            // 1. Find the existing product using the String ID
            ProductModel existingProduct = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

            // 2. Update fields only if they are provided in the request
            if (name != null) existingProduct.setName(name);
            if (category != null) existingProduct.setCategory(category);
            if (description != null) existingProduct.setDescription(description);
            if (itemCount != null) existingProduct.setItemCount(itemCount);
            if (gram != null) existingProduct.setGram(gram);

            // 3. Handle optional image upload
            if (file != null && !file.isEmpty()) {
                String newImageUrl = cloudinaryService.uploadImage(file);
                existingProduct.setImageUrl(newImageUrl);
            }

            // 4. Recalculate price in case the gram or category was updated
            double perGramPrice = pricingRepository.findByName(existingProduct.getCategory().name())
                    .orElseThrow(() -> new RuntimeException("Pricing data not found for: " + existingProduct.getCategory().name()))
                    .getCurrentPricingPerGram();

            double makingCharges = 1000;
            double newPrice = (existingProduct.getGram() * perGramPrice) + makingCharges;
            existingProduct.setPrice(newPrice);

            // 5. Save and return
            ProductModel updatedProduct = productRepository.save(existingProduct);
            return ResponseEntity.ok(updatedProduct);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error updating product: " + e.getMessage());
        }
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") String id) { // FIXED: Now accepts MongoDB String IDs
        try {
            // 1. Find the product using the String ID
            ProductModel existingProduct = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

            // 2. Delete from database
            productRepository.delete(existingProduct);

            // 3. Return a success message
            return ResponseEntity.ok().body("Product with id " + id + " deleted successfully.");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error deleting product: " + e.getMessage());
        }
    }

    @GetMapping("/wishlist/{id}")
    public ResponseEntity<?> getWishList(@PathVariable String id){
        UserModel user = userRepository.findById(id).orElse(null);
        if(user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        return ResponseEntity.ok(user.getProducts());
    }

    @GetMapping("/purchaseHistory/{id}")
    public ResponseEntity<?> getPurchaseHistory(@PathVariable String id){
        UserModel user = userRepository.findById(id).orElse(null);
        if(user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        return ResponseEntity.ok(user.getPurchaseHistory());
    }
}
