package org.example.ebookstore.controllers;

import org.example.ebookstore.services.interfaces.OrderService;
import org.example.ebookstore.services.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class OrderController {
    private final UserService userService;
    private final OrderService orderService;

    @Autowired
    public OrderController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @PostMapping("/books/{bookId}/purchase")
    public ResponseEntity<?> buyBook(@PathVariable("bookId") Long bookId, Model model) {
        try {
            this.orderService.createOrder(bookId, model);
            return ResponseEntity.ok("You have successfully purchased this book. You can now read it in the " +
                    "Kindle app. Please visit your profile, if you would like to view your orders or the books " +
                    "that you own.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
