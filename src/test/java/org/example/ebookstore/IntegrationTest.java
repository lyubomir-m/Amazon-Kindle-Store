package org.example.ebookstore;

import org.example.ebookstore.entities.Book;
import org.example.ebookstore.entities.User;
import org.example.ebookstore.entities.dtos.ReviewSubmissionDto;
import org.example.ebookstore.repositories.BookRepository;
import org.example.ebookstore.repositories.UserRepository;
import org.example.ebookstore.services.interfaces.ReviewService;
import org.example.ebookstore.services.interfaces.ShoppingCartService;
import org.example.ebookstore.services.interfaces.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private ReviewService reviewService;

    @Test
    @Transactional
    public void testUserActions() throws Exception {
        // Retrieve the user with id=3
        User user = userRepository.findById(3L).orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Retrieve books with id=100 and id=101
        Book book1 = bookRepository.findById(100L).orElseThrow(() -> new IllegalArgumentException("Book not found"));
        Book book2 = bookRepository.findById(101L).orElseThrow(() -> new IllegalArgumentException("Book not found"));

        // Add both books to the user's wishlist
        user.getWishlist().addBook(book1);
        user.getWishlist().addBook(book2);
        userRepository.save(user);
        assertThat(user.getWishlist().getBooks()).containsExactlyInAnyOrder(book1, book2);

        // Add both books to the user's shopping cart
        user.getShoppingCart().addBook(book1);
        user.getShoppingCart().addBook(book2);
        userRepository.save(user);

        // Simulate the checkout process
        shoppingCartService.buyAllBooksInShoppingCart(user);

        // Simulate the review process for both books
        ReviewSubmissionDto review1 = new ReviewSubmissionDto(user.getId(), book1.getId(), "Great book!", 5, null);
        ReviewSubmissionDto review2 = new ReviewSubmissionDto(user.getId(), book2.getId(), "Excellent read!", 5, null);
        reviewService.createReview(review1, null, null);
        reviewService.createReview(review2, null, null);

        // Assertions to verify the actions
        assertThat(user.getShoppingCart().getBooks()).isEmpty();
        assertThat(book1.getReviews()).isNotEmpty();
        assertThat(book2.getReviews()).isNotEmpty();
    }
}