package org.example.ebookstore.controllers;

import org.example.ebookstore.entities.Currency;
import org.example.ebookstore.entities.dtos.BookDto;
import org.example.ebookstore.entities.dtos.UserDto;
import org.example.ebookstore.services.interfaces.BookService;
import org.example.ebookstore.services.interfaces.CommonService;
import org.example.ebookstore.services.interfaces.WishlistService;
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
public class WishlistController {
    private final WishlistService wishlistService;
    private final BookService bookService;
    private final CommonService commonService;

    @Autowired
    public WishlistController(WishlistService wishlistService, BookService bookService, CommonService commonService) {
        this.wishlistService = wishlistService;
        this.bookService = bookService;
        this.commonService = commonService;
    }

    @GetMapping("/users/list")
    public String displayWishlistPage(Model model, @RequestParam(defaultValue = "0") int page) {
        UserDto userDto = (UserDto) model.getAttribute("userDto");
        if (userDto == null) {
            return "error";
        }
        Long wishlistId = userDto.getWishlist().getId();
        Currency currency = userDto.getSelectedCurrency();
        Pageable pageable = PageRequest.of(page, 50);
        Page<BookDto> bookDtoPage = this.bookService.findByWishlistId(wishlistId, pageable, currency);

        this.commonService.addItemAttributesToModel(model, bookDtoPage, pageable, page);

        return "wishlist";
    }

    @PostMapping("/books/{bookId}/list")
    public ResponseEntity<?> addBookToWishlist(@PathVariable("bookId") Long bookId, Model model) {
        try {
            this.wishlistService.addBookToWishlist(bookId, model);
            return ResponseEntity.ok().body("The book was added to your list.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/books/{bookId}/list")
    public ResponseEntity<?> removeBookFromWishlist(@PathVariable("bookId") Long bookId, Model model) {
        try {
            this.wishlistService.removeBookFromWishlist(bookId, model);
            return ResponseEntity.ok().body("The book was removed from your list.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
