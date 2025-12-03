package org.example.ebookstore.services.implementations;

import org.example.ebookstore.entities.Book;
import org.example.ebookstore.entities.User;
import org.example.ebookstore.entities.Wishlist;
import org.example.ebookstore.entities.dtos.UserDto;
import org.example.ebookstore.repositories.BookRepository;
import org.example.ebookstore.repositories.WishlistRepository;
import org.example.ebookstore.services.interfaces.UserService;
import org.example.ebookstore.services.interfaces.WishlistService;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class WishlistServiceImpl implements WishlistService {
    private final WishlistRepository wishlistRepository;
    private final UserService userService;
    private final BookRepository bookRepository;

    public WishlistServiceImpl(WishlistRepository wishlistRepository, UserService userService, BookRepository bookRepository) {
        this.wishlistRepository = wishlistRepository;
        this.userService = userService;
        this.bookRepository = bookRepository;
    }

    @Override
    public void addBookToWishlist(Long bookId, Model model) {
        UserDto userDto = (UserDto) model.getAttribute("userDto");
        if (userDto == null) {
            throw new IllegalArgumentException("You have to be logged in.");
        }
        Book book = this.bookRepository.findById(bookId).get();
        Wishlist wishlist = this.wishlistRepository.findById(userDto.getWishlist().getId()).get();

        if (this.userService.hasUserPurchasedBook(userDto.getId(), bookId)) {
            throw new IllegalArgumentException("You already own this book.");
        }
        if (wishlist.getBooks().contains(book)) {
            throw new IllegalArgumentException("This book is already in your list.");
        }

        wishlist.addBook(book);
        book.addWishlists(wishlist);

        this.bookRepository.save(book);
        this.wishlistRepository.save(wishlist);
    }

    @Override
    public void removeBookFromWishlist(Long bookId, Model model) {
        UserDto userDto = (UserDto) model.getAttribute("userDto");
        if (userDto == null) {
            throw new IllegalArgumentException("You have to be logged in.");
        }
        Book book = this.bookRepository.findById(bookId).get();
        Wishlist wishlist = this.wishlistRepository.findById(userDto.getWishlist().getId()).get();

        if (!wishlist.getBooks().contains(book)) {
            throw new IllegalArgumentException("This book is not in your list.");
        }

        wishlist.removeBook(book);
        book.removeWishlists(wishlist);

        this.bookRepository.save(book);
        this.wishlistRepository.save(wishlist);
    }
}
