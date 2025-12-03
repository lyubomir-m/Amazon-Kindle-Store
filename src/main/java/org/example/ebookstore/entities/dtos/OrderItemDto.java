package org.example.ebookstore.entities.dtos;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.example.ebookstore.entities.Book;
import org.example.ebookstore.entities.Order;

import java.math.BigDecimal;

public class OrderItemDto {
    private Long id;
    private OrderDto order;
    private BookDto book;
    private BigDecimal price;
    private Integer insertionOrder;

    public OrderItemDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderDto getOrder() {
        return order;
    }

    public void setOrder(OrderDto order) {
        this.order = order;
    }

    public BookDto getBook() {
        return book;
    }

    public void setBook(BookDto book) {
        this.book = book;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getInsertionOrder() {
        return insertionOrder;
    }

    public void setInsertionOrder(Integer insertionOrder) {
        this.insertionOrder = insertionOrder;
    }
}
