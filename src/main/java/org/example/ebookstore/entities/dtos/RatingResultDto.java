package org.example.ebookstore.entities.dtos;

public class RatingResultDto {
    private double averageRating;
    private long ratingsCount;

    public RatingResultDto() {
    }

    public RatingResultDto(double averageRating, long ratingsCount) {
        this.averageRating = averageRating;
        this.ratingsCount = ratingsCount;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public long getRatingsCount() {
        return ratingsCount;
    }

    public void setRatingsCount(long ratingsCount) {
        this.ratingsCount = ratingsCount;
    }
}
