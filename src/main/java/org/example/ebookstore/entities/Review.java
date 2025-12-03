package org.example.ebookstore.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
@Table(name = "reviews")
public class Review extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private User user;
    @Column(nullable = false, length = 100)
    @Size(min = 5, max = 100)
    @NotNull
    private String title;
    @Column(nullable = false, length = 700)
    @Size(min = 5, max = 700)
    @NotNull
    private String text;
    @Column(name = "submission_date", nullable = false)
    @NotNull
    private LocalDate submissionDate;
    @OneToOne
    @JoinColumn(name = "rating_id", nullable = false)
    @NotNull
    private Rating rating;
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    public Review() {
    }

    public Review(User user, String text, LocalDate submissionDate, Rating rating) {
        this.user = user;
        this.text = text;
        this.submissionDate = submissionDate;
        this.rating = rating;
    }

    public Review(User user, String title, String text, LocalDate submissionDate, Rating rating, Book book) {
        this.user = user;
        this.title = title;
        this.text = text;
        this.submissionDate = submissionDate;
        this.rating = rating;
        this.book = book;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
