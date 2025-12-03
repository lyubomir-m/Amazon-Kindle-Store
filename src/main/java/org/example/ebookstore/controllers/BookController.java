package org.example.ebookstore.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.example.ebookstore.entities.Currency;
import org.example.ebookstore.entities.dtos.BookDto;
import org.example.ebookstore.entities.dtos.ReviewDto;
import org.example.ebookstore.services.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
public class BookController {
    private final BookService bookService;
    private final UserService userService;
    private final ReviewService reviewService;
    private final CommonService commonService;

    @Autowired
    public BookController(BookService bookService, UserService userService, ReviewService reviewService, CommonService commonService) {
        this.bookService = bookService;
        this.userService = userService;
        this.reviewService = reviewService;
        this.commonService = commonService;
    }

    @GetMapping("/books/{id}")
    @Transactional
    public String bookDetails(@PathVariable("id") Long id, Model model, HttpServletRequest request,
                              @RequestParam(defaultValue = "0") int page) {

        Currency currency = this.userService.getSelectedCurrency(request);
        Optional<BookDto> bookDto = this.bookService.getDto(id, currency);
        if (bookDto.isPresent()) {
            model.addAttribute("book", bookDto.get());

            List<BookDto> books = this.bookService.getRecommendedBooks(id, currency);
            model.addAttribute("books", books);
            Pageable pageable = PageRequest.of(page, 10);

            Page<ReviewDto> reviewPage = this.reviewService.getReviewsByBookId(id, pageable);
            List<ReviewDto> reviews = new ArrayList<>(reviewPage.getContent());
            int placeholderCount = 10 - reviews.size();
            List<ReviewDto> placeholderReviews = this.reviewService.getPlaceholderReviews(placeholderCount);
            reviews.addAll(placeholderReviews);

            model.addAttribute("reviews", reviews);
            model.addAttribute("currentReviewPage", pageable.getPageNumber());
            model.addAttribute("totalReviewPages", reviewPage.getTotalPages());

            return "book-details";
        } else {
            return "error";
        }
    }

    @GetMapping("/search")
    public String displaySearchResults(Model model, @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "purchaseCountDesc") String sortBy,
                                       @RequestParam String query) {

        Currency currency = (Currency) model.getAttribute("selectedCurrency");
        Sort sort = this.commonService.getSortByParameter(sortBy);
        Pageable pageable = PageRequest.of(page, 16, sort);
        Page<BookDto> bookDtoPage = this.bookService.findBySearchQuery(query, pageable, currency);

        this.commonService.addBookPageAttributesToModel(model, bookDtoPage, pageable, page, sortBy);
        model.addAttribute("query", query);
        return "book-search";
    }
}
