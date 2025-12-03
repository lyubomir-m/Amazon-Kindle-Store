package org.example.ebookstore.services.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import org.example.ebookstore.entities.Currency;
import org.example.ebookstore.entities.User;
import org.example.ebookstore.entities.dtos.UserDto;
import org.example.ebookstore.entities.dtos.UserRegistrationDto;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long userId);
    Optional<User> findByEmail(String email);
    User save(User user);
    Optional<UserDto> getUserDtoByUsername(String username);
    Currency getSelectedCurrency(HttpServletRequest request);
    void updateUserCurrency(Long userId, Currency currency);
    Long getUserId(Model model, Authentication authentication);
    boolean hasUserPurchasedBook(Long userId, Long bookId);
    boolean hasUserRatedBook(Long userId, Long bookId);
    boolean hasUserReviewedBook(Long userId, Long bookId);
    User createUser(UserRegistrationDto userRegistrationDto);
    List<UserDto> findAll();
    void addAdminRoleToUser(Model model, Long userId);
    void removeAdminRoleFromUser(Model model, Long userId);
}
