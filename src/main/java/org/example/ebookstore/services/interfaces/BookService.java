package org.example.ebookstore.services.interfaces;

import org.example.ebookstore.entities.Book;
import org.example.ebookstore.entities.Currency;
import org.example.ebookstore.entities.dtos.BookDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BookService {
    BigDecimal round(BigDecimal value);
    List<BookDto> findFirst54BestSellers(Currency currency);
    Optional<BookDto> getDto(Long id, Currency currency);
    BookDto mapBookToDto(Book book, Currency currency);
    Page<BookDto> findByCategoryId(Long categoryId, Pageable pageable, Currency currency);
    List<BookDto> getRecommendedBooks(Long bookId, Currency currency);
    Page<BookDto> findByAuthorId(Long authorId, Pageable pageable, Currency currency);
    Page<BookDto> findByPublisherId(Long publisherId, Pageable pageable, Currency currency);
    BigDecimal getPriceInSelectedCurrency(Book book, Currency currency);
    Page<BookDto> findByUserId(Long userId, Pageable pageable, Currency currency);
    Page<BookDto> findByShoppingCartId(Long cartId, Pageable pageable, Currency currency);
    Page<BookDto> findByWishlistId(Long wishlistId, Pageable pageable, Currency currency);
    void updateFxPricesOfAllBooks();
    Page<BookDto> findBySearchQuery(String query, Pageable pageable, Currency currency);
}
