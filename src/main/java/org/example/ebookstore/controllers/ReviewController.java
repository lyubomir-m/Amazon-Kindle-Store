package org.example.ebookstore.controllers;

import jakarta.validation.Valid;
import org.example.ebookstore.entities.dtos.ReviewResultDto;
import org.example.ebookstore.entities.dtos.ReviewSubmissionDto;
import org.example.ebookstore.services.interfaces.RatingService;
import org.example.ebookstore.services.interfaces.ReviewService;
import org.example.ebookstore.services.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ReviewController {
    private final ReviewService reviewService;
    private final UserService userService;
    private final RatingService ratingService;

    @Autowired
    public ReviewController(ReviewService reviewService, UserService userService, RatingService ratingService) {
        this.reviewService = reviewService;
        this.userService = userService;
        this.ratingService = ratingService;
    }

    @GetMapping("/books/{bookId}/check-review")
    public ResponseEntity<?> checkReviewCreation(@PathVariable("bookId") Long bookId, Model model) {
        try {
            this.reviewService.checkReviewCreation(model, bookId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/books/{bookId}/review")
    public ResponseEntity<?> reviewBook(@PathVariable("bookId") Long bookId,
                                        @Valid @RequestBody ReviewSubmissionDto reviewSubmissionDto,
                                        Model model) {
        try {
            ReviewResultDto result = this.reviewService.createReview(reviewSubmissionDto, model, bookId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/books/{bookId}/review")
    public ResponseEntity<?> deleteReview(@PathVariable("bookId") Long bookId, Model model) {
        try {
            String response = this.reviewService.deleteReview(model, bookId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PutMapping("/books/{bookId}/review/")
    public ResponseEntity<?> updateReview(@PathVariable Long bookId,
                                          @Valid @RequestBody ReviewSubmissionDto reviewSubmissionDto,
                                          Model model) {
        try {
            this.reviewService.updateReview(reviewSubmissionDto, model, bookId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
