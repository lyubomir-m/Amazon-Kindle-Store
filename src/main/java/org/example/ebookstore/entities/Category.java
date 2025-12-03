package org.example.ebookstore.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.*;

@Entity
@Table(name = "categories")
public class Category extends BaseEntity {
    @Column(unique = true, nullable = false, length = 80)
    @Size(min = 3, max = 80)
    private String name;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;
    @OneToMany(mappedBy = "parent")
    private Set<Category> subcategories = new HashSet<>();
    @ManyToMany(mappedBy = "categories")
    private Set<Book> books = new HashSet<>();

    public Category() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public Set<Category> getSubcategories() {
        return Collections.unmodifiableSet(this.subcategories);
    }
    public void addSubcategory(Category subcategory) {
        this.subcategories.add(subcategory);
    }
    public void removeSubcategory(Category subcategory) {
        this.subcategories.remove(subcategory);
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
