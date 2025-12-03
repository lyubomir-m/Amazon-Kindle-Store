package org.example.ebookstore.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    @Column(precision = 19, scale = 4, nullable = false)
    private BigDecimal price;
    @Column(name = "insertion_order", nullable = false)
    private Integer insertionOrder;

    public OrderItem() {
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
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
