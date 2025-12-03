package org.example.ebookstore.entities.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReviewSubmissionDto {
    @Min(1)
    @Max(5)
    @NotNull
    private int reviewRating;
    @Size(min = 5, max = 100)
    @NotNull
    private String reviewTitle;
    @Size(min = 5, max = 700)
    @NotNull
    private String reviewText;

    public ReviewSubmissionDto() {
    }

    public ReviewSubmissionDto(int reviewRating, String reviewTitle, String reviewText) {
        this.reviewRating = reviewRating;
        this.reviewTitle = reviewTitle;
        this.reviewText = reviewText;
    }

    public int getReviewRating() {
        return reviewRating;
    }

    public void setReviewRating(int reviewRating) {
        this.reviewRating = reviewRating;
    }

    public String getReviewTitle() {
        return reviewTitle;
    }

    public void setReviewTitle(String reviewTitle) {
        this.reviewTitle = reviewTitle;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }
}
