package org.sharmas.jwelleryshopbe.controller;

import com.mongodb.DuplicateKeyException;
import jakarta.validation.Valid;
import org.sharmas.jwelleryshopbe.models.ProductModel;
import org.sharmas.jwelleryshopbe.models.UserModel;
import org.sharmas.jwelleryshopbe.repository.ProductRepository;
import org.sharmas.jwelleryshopbe.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.productRepository = productRepository;
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserDetails(@PathVariable String username){
        UserModel currentUSer = userRepository.findByUsername(username).orElse(null);
        if(currentUSer == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        return ResponseEntity.ok(currentUSer);
    }
//    @GetMapping("/user/{id}")
//    public ResponseEntity<?> getUserDetails(@PathVariable String id){
//        UserModel currentUSer = userRepository.findById(id).orElse(null);
//        if(currentUSer == null){
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
//        }
//        return ResponseEntity.ok(currentUSer);
//    }

    @PostMapping("/user")
    public ResponseEntity<?> creteUser(@Valid @RequestBody UserModel userDetails) {
        try {
            String rawPassword = userDetails.getPassword();
            String hashedPassword = passwordEncoder.encode(rawPassword);
            userDetails.setPassword(hashedPassword);
            userDetails.setRole(UserModel.Role.USER);

            UserModel newUser = userRepository.save(userDetails);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Error: A user with this email or username already exists!");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: Something went wrong while saving the user.");
        }
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<?> changeUserName(@PathVariable String id, @RequestBody UserModel updateUserDetails) {
        return userRepository.findById(id).map(user -> {
            user.setUsername(updateUserDetails.getUsername());
            user.setEmail(updateUserDetails.getEmail());

            UserModel savedUser = userRepository.save(user);
            return ResponseEntity.ok(savedUser);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PutMapping("/user/Forgotpassword/{id}")
    public ResponseEntity<?> updatePassword(@PathVariable String id, @RequestBody Map<String, String> requestBody) {

        // 1. Find the user
        UserModel user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: User not found!");
        }

        // 2. Extract EXACTLY what React sent using the correct keys
        String oldPassword = requestBody.get("oldPassword");
        String newPassword = requestBody.get("newPassword");

        if (newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("New password cannot be empty");
        }

        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Current password is required");
        }

        // 3. SECURITY CHECK: Verify the old password
        boolean isPasswordCorrect = passwordEncoder.matches(oldPassword, user.getPassword());

        if (!isPasswordCorrect) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect current password.");
        }

        // 4. Update and save
        String hashedNewPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedNewPassword);
        userRepository.save(user);

        return ResponseEntity.ok("Password updated successfully");
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok("User successfully deleted!");
    }

    @PutMapping("user/{id_user}/wishlist/{id_item}")
    public ResponseEntity<?> addToCart(@PathVariable String id_user,@PathVariable String id_item){
        UserModel user = userRepository.findById(id_user).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: User not found!");
        }
        ProductModel item = productRepository.findById(id_item).orElse(null);
        if (item == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Item not found!");
        }

        if(user.getProducts().stream().anyMatch(p -> p.getId().equals(id_item))){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Product is already in the wishlist");
        }

        user.getProducts().add(item);
        userRepository.save(user);
        return ResponseEntity.ok("Product added to the wishlist successfully");
    }

    @DeleteMapping("user/{id_user}/wishlist/{id_item}")
    public ResponseEntity<?> deleteFromCart(@PathVariable String id_user,@PathVariable String id_item){
        UserModel user = userRepository.findById(id_user).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: User not found!");
        }
        ProductModel item = productRepository.findById(id_item).orElse(null);
        if (item == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Item not found!");
        }

        user.getProducts().remove(item);
        userRepository.save(user);
        return ResponseEntity.ok("Product removed to the wishlist successfully");
    }
}