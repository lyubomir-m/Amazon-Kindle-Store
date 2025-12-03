package org.example.ebookstore.entities.dtos;

import org.example.ebookstore.entities.Book;

import java.util.*;

public class ShoppingCartDto {
    private Long id;
    private UserDto user;
    private List<Book> books = new ArrayList<>();

    public ShoppingCartDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
