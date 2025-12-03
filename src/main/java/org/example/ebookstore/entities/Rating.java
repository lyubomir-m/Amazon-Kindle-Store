package org.example.ebookstore.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;

@Entity
@Table(name = "ratings")
public class Rating extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "submission_date", nullable = false)
    private LocalDate submissionDate;
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
    @Column(name = "rating_value", nullable = false)
    @Min(1)
    @Max(5)
    private Integer ratingValue;

    public Rating() {
    }

    public Rating(User user, LocalDate submissionDate, Integer ratingValue) {
        this.user = user;
        this.submissionDate = submissionDate;
        this.ratingValue = ratingValue;
    }

    public Rating(User user, LocalDate submissionDate, Book book, Integer ratingValue) {
        this.user = user;
        this.submissionDate = submissionDate;
        this.book = book;
        this.ratingValue = ratingValue;
    }

    public Integer getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(Integer ratingValue) {
        this.ratingValue = ratingValue;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
