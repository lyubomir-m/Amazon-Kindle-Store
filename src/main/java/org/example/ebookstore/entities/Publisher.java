package org.example.ebookstore.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "publishers", indexes = @Index(name = "idx_publisher_name", columnList = "name"))
public class Publisher extends BaseEntity {
    @Column(unique = true, nullable = false, length = 128)
    @Size(min = 5, max = 128)
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    @OneToMany(mappedBy = "publisher")
    private Set<Book> books = new HashSet<>();
    @Column(name = "logo_color", nullable = false, length = 30)
    @Size(min = 1, max = 30)
    private String logoColor;

    public Publisher() {
    }

    public String getLogoColor() {
        return logoColor;
    }

    public void setLogoColor(String logoColor) {
        this.logoColor = logoColor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}
