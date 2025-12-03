package org.example.ebookstore.services.interfaces;

import org.example.ebookstore.entities.ShoppingCart;
import org.springframework.ui.Model;

public interface ShoppingCartService {
    int addBookToShoppingCart(Long bookId, Model model);
    void buyAllBooksInShoppingCart(Model model);
    int removeBookFromShoppingCart(Long bookId, Model model);
}
