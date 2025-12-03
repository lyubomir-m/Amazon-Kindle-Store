package org.example.ebookstore.entities;

import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "wishlists")
public class Wishlist extends BaseEntity {
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToMany
    @JoinTable(
            name = "wishlists_books",
            joinColumns = @JoinColumn(name = "wishlist_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    @OrderColumn(name = "insertion_order")
    private List<Book> books = new ArrayList<>();

    public Wishlist() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Book> getBooks() {
        return Collections.unmodifiableList(this.books);
    }
    public void addBook(Book book) {
        if (this.books.contains(book)) {
            return;
        }
        this.books.add(book);
    }
    public void removeBook(Book book) {
        this.books.remove(book);
    }
}
