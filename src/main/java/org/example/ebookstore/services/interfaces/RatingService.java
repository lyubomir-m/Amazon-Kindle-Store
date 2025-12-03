package org.example.ebookstore.services.interfaces;

import org.example.ebookstore.entities.dtos.RatingDto;
import org.example.ebookstore.entities.dtos.RatingResultDto;
import org.example.ebookstore.entities.dtos.RatingSubmissionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;

public interface RatingService {
    RatingResultDto createRating(int ratingValue, Model model, Long bookId);
    String deleteRating(Long bookId, Model model);
    void checkRatingCreation(Model model, Long bookId);
    Page<RatingDto> findByUserId(Long userId, Pageable pageable);
    void updateRating(RatingSubmissionDto ratingSubmissionDto, Model model, Long bookId);
}
