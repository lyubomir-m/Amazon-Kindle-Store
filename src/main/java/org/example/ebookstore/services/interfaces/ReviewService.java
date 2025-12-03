package org.example.ebookstore.services.interfaces;

import org.example.ebookstore.entities.Review;
import org.example.ebookstore.entities.dtos.ReviewDto;
import org.example.ebookstore.entities.dtos.ReviewResultDto;
import org.example.ebookstore.entities.dtos.ReviewSubmissionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;

import java.util.List;

public interface ReviewService {
    List<ReviewDto> getPlaceholderReviews(int count);
    Page<ReviewDto> getReviewsByBookId(Long bookId, Pageable pageable);
    ReviewResultDto createReview(ReviewSubmissionDto reviewSubmissionDto, Model model, Long bookId);
    void checkReviewCreation(Model model, Long bookId);
    String deleteReview(Model model, Long bookId);
    Page<ReviewDto> findByUserId(Long userId, Pageable pageable);
    void updateReview(ReviewSubmissionDto reviewSubmissionDto, Model model, Long bookId);
}
