package org.example.ebookstore.controllers;

import org.example.ebookstore.entities.Currency;
import org.example.ebookstore.entities.dtos.BookDto;
import org.example.ebookstore.entities.dtos.UserDto;
import org.example.ebookstore.services.interfaces.BookService;
import org.example.ebookstore.services.interfaces.CommonService;
import org.example.ebookstore.services.interfaces.ShoppingCartService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ShoppingCartController {
   private final ShoppingCartService shoppingCartService;
   private final BookService bookService;
   private final CommonService commonService;

    public ShoppingCartController(ShoppingCartService shoppingCartService, BookService bookService, CommonService commonService) {
        this.shoppingCartService = shoppingCartService;
        this.bookService = bookService;
        this.commonService = commonService;
    }

    @GetMapping("/users/cart")
    public String displayShoppingCartPage(Model model, @RequestParam(defaultValue = "0") int page) {
        UserDto userDto = (UserDto) model.getAttribute("userDto");
        if (userDto == null) {
            return "error";
        }
        Long cartId = userDto.getShoppingCart().getId();
        Currency currency = userDto.getSelectedCurrency();
        Pageable pageable = PageRequest.of(page, 50);
        Page<BookDto> bookDtoPage = this.bookService.findByShoppingCartId(cartId, pageable, currency);

        this.commonService.addItemAttributesToModel(model, bookDtoPage, pageable, page);

        return "shopping-cart";
    }

    @PostMapping("/books/{bookId}/cart")
    public ResponseEntity<?> addBookToShoppingCart(@PathVariable("bookId") Long bookId, Model model) {
        try {
            int itemCount = this.shoppingCartService.addBookToShoppingCart(bookId, model);
            return ResponseEntity.ok().body(itemCount);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/books/{bookId}/cart")
    public ResponseEntity<?> removeBookFromShoppingCart(@PathVariable("bookId") Long bookId, Model model) {
        try {
            int itemCount = this.shoppingCartService.removeBookFromShoppingCart(bookId, model);
            return ResponseEntity.ok().body(itemCount);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/users/cart/checkout")
    public ResponseEntity<?> buyAllBooksInShoppingCart(Model model) {
        try {
            this.shoppingCartService.buyAllBooksInShoppingCart(model);
            return ResponseEntity.ok().body("You have successfully purchased all books in your shopping cart. " +
                    "You can now read them in the " +
                    "Kindle app. Please visit your profile, if you would like to view your orders or the books " +
                    "that you own.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
