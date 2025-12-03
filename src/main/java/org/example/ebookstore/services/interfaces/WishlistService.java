package org.example.ebookstore.services.interfaces;

import org.springframework.ui.Model;

public interface WishlistService {
    void addBookToWishlist(Long bookId, Model model);
    void removeBookFromWishlist(Long bookId, Model model);
}
