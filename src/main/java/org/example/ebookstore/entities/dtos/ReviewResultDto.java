package org.example.ebookstore.entities.dtos;

import java.time.LocalDate;

public class ReviewResultDto {
    private double averageRating;
    private long ratingsCount;
    private String pictureBase64;
    private String firstName;
    private String lastName;
    private int ratingValue;
    private String title;
    private String text;
    private LocalDate submissionDate;

    public ReviewResultDto() {
    }

    public ReviewResultDto(double averageRating, long ratingsCount, String pictureBase64, String firstName, String lastName, int ratingValue, String title, String text, LocalDate submissionDate) {
        this.averageRating = averageRating;
        this.ratingsCount = ratingsCount;
        this.pictureBase64 = pictureBase64;
        this.firstName = firstName;
        this.lastName = lastName;
        this.ratingValue = ratingValue;
        this.title = title;
        this.text = text;
        this.submissionDate = submissionDate;
    }

    public String getPictureBase64() {
        return pictureBase64;
    }

    public void setPictureBase64(String pictureBase64) {
        this.pictureBase64 = pictureBase64;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(int ratingValue) {
        this.ratingValue = ratingValue;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
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
