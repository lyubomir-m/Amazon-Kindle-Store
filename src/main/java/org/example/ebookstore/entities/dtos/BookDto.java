package org.example.ebookstore.entities.dtos;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.example.ebookstore.entities.*;
import org.example.ebookstore.entities.Currency;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class BookDto {
    private Long Id;
    private List<AuthorDto> authors = new ArrayList<>();
    private Double averageRating;
    private Long ratingsCount;
    private String description;
    private String title;
    private LocalDate publicationDate;
    private PublisherDto publisher;

    private Long purchaseCount;
    private String imageUrl;
    private String coverColor;
    private BigDecimal selectedCurrencyPrice;
    private Currency selectedCurrency;

    private int fullStars;
    private int halfStar;
    private int emptyStars;

    public BookDto() {
    }

    public Currency getSelectedCurrency() {
        return selectedCurrency;
    }

    public void setSelectedCurrency(Currency selectedCurrency) {
        this.selectedCurrency = selectedCurrency;
    }

    public BigDecimal getSelectedCurrencyPrice() {
        return selectedCurrencyPrice;
    }

    public void setSelectedCurrencyPrice(BigDecimal price) {
        this.selectedCurrencyPrice = price;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public List<AuthorDto> getAuthors() {
        return authors;
    }

    public void setAuthors(List<AuthorDto> authors) {
        this.authors = authors.stream().sorted(Comparator.comparing(AuthorDto::getId))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Long getRatingsCount() {
        return ratingsCount;
    }

    public void setRatingsCount(Long ratingsCount) {
        this.ratingsCount = ratingsCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public PublisherDto getPublisher() {
        return publisher;
    }

    public void setPublisher(PublisherDto publisher) {
        this.publisher = publisher;
    }

    public Long getPurchaseCount() {
        return purchaseCount;
    }

    public void setPurchaseCount(Long purchaseCount) {
        this.purchaseCount = purchaseCount;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCoverColor() {
        return coverColor;
    }

    public void setCoverColor(String coverColor) {
        this.coverColor = coverColor;
    }


    public int getFullStars() {
        return fullStars;
    }

    public void setFullStars(int fullStars) {
        this.fullStars = fullStars;
    }

    public int getHalfStar() {
        return halfStar;
    }

    public void setHalfStar(int halfStar) {
        this.halfStar = halfStar;
    }

    public int getEmptyStars() {
        return emptyStars;
    }

    public void setEmptyStars(int emptyStars) {
        this.emptyStars = emptyStars;
    }
}
