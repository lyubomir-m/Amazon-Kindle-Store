package org.example.ebookstore.controllers;

import jakarta.validation.Valid;
import org.example.ebookstore.entities.Currency;
import org.example.ebookstore.entities.dtos.*;
import org.example.ebookstore.services.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final CommonService commonService;
    private final BookService bookService;
    private final RatingService ratingService;
    private final ReviewService reviewService;
    private final OrderService orderService;

    @Autowired
    public UserController(UserService userService, CommonService commonService, BookService bookService, RatingService ratingService, ReviewService reviewService, OrderService orderService) {
        this.userService = userService;
        this.commonService = commonService;
        this.bookService = bookService;
        this.ratingService = ratingService;
        this.reviewService = reviewService;
        this.orderService = orderService;
    }

    @GetMapping("/login")
    public String displayLoginPage() {
        return "user-log-in";
    }

    @GetMapping("/register")
    public String displayRegistrationPage() {
        return "user-register";
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDto userRegistrationDto) {
        try {
            this.userService.createUser(userRegistrationDto);
            return ResponseEntity.ok("You have registered successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/profile")
    public String displayUserProfileUpdatePage(Model model) {
        return "user-profile";
    }

    @GetMapping("/books")
    public String displayUserBooksPage(Model model,
                                       @RequestParam(defaultValue = "0") int page) {
        UserDto userDto = (UserDto) model.getAttribute("userDto");
        if (userDto == null) {
            return "error";
        }
        Long userId = userDto.getId();
        Currency currency = userDto.getSelectedCurrency();
        Pageable pageable = PageRequest.of(page, 16);
        Page<BookDto> bookDtoPage = this.bookService.findByUserId(userId, pageable, currency);

        this.commonService.addItemAttributesToModel(model, bookDtoPage, pageable, page);

        return "user-books";
    }

    @GetMapping("/orders")
    public String displayUserOrdersPage(Model model,
                                        @RequestParam(defaultValue = "0") int page) {
        UserDto userDto = (UserDto) model.getAttribute("userDto");
        if (userDto == null) {
            return "error";
        }
        Long userId = userDto.getId();
        Pageable pageable = PageRequest.of(page, 16);
        Page<OrderDto> orderDtoPage = this.orderService.findByUserId(userId, pageable);

        this.commonService.addItemAttributesToModel(model, orderDtoPage, pageable, page);

        return "user-orders";
    }

    @GetMapping("/ratings")
    public String displayUserRatingsPage(Model model,
                                         @RequestParam(defaultValue = "0") int page) {
        UserDto userDto = (UserDto) model.getAttribute("userDto");
        if (userDto == null) {
            return "error";
        }
        Long userId = userDto.getId();
        Pageable pageable = PageRequest.of(page, 16);
        Page<RatingDto> ratingDtoPage = this.ratingService.findByUserId(userId, pageable);

        this.commonService.addItemAttributesToModel(model, ratingDtoPage, pageable, page);

        return "user-ratings";
    }

    @GetMapping("/reviews")
    public String displayUserReviewsPage(Model model,
                                         @RequestParam(defaultValue = "0") int page) {
        UserDto userDto = (UserDto) model.getAttribute("userDto");
        if (userDto == null) {
            return "error";
        }
        Long userId = userDto.getId();
        Pageable pageable = PageRequest.of(page, 16);
        Page<ReviewDto> reviewDtoPage = this.reviewService.findByUserId(userId, pageable);

        this.commonService.addItemAttributesToModel(model, reviewDtoPage, pageable, page);

        return "user-reviews";
    }
}
