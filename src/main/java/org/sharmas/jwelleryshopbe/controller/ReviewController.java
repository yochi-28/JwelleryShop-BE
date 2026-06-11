package org.sharmas.jwelleryshopbe.controller;

import org.sharmas.jwelleryshopbe.models.ReviewModel;
import org.sharmas.jwelleryshopbe.models.UserModel;
import org.sharmas.jwelleryshopbe.repository.ReviewRepository;
import org.sharmas.jwelleryshopbe.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public ReviewController(ReviewRepository reviewRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/reviews")
    public ResponseEntity<List<ReviewModel>> getAllReviews() {
        List<ReviewModel> reviews = reviewRepository.findAllByOrderByCreatedAtDesc();
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/reviews")
    public ResponseEntity<ReviewModel> submitReview(@RequestBody ReviewModel review) {
        // Fetch the user from the database using the userId
        UserModel user = userRepository.findById(review.getUserId()).orElse(null);
        if (user != null) {
            review.setUsername(user.getUsername());   // snapshot username
        }
        review.setCreatedAt(new Date());
        ReviewModel saved = reviewRepository.save(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable String id) {
        if (!reviewRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        reviewRepository.deleteById(id);
        return ResponseEntity.ok("Review deleted");
    }
}