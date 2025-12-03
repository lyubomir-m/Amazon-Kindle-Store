package org.example.ebookstore.entities.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class RatingSubmissionDto {
    @Min(1)
    @Max(5)
    @NotNull
    private int rating;

    public RatingSubmissionDto() {
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
