package org.example.ebookstore.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "authors", indexes = @Index(name = "idx_author_full_name", columnList = "full_name"))
public class Author extends BaseEntity {
    @Column(name = "full_name", nullable = false, length = 50)
    @Size(min = 5, max = 50)
    private String fullName;
    @ManyToMany(mappedBy = "authors")
    private Set<Book> books = new HashSet<>();
    @Column(columnDefinition = "TEXT")
    private String description;
    @ManyToOne
    @JoinColumn(name = "picture_id")
    private Picture picture;

    public Author() {
    }

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Set<Book> getBooks() {
        return Collections.unmodifiableSet(this.books);
    }

    public void addBook(Book book) {
        this.books.add(book);
    }
    public void removeBook(Book book) {
        this.books.remove(book);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
