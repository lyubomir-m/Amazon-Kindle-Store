package org.example.ebookstore.services.implementations;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.example.ebookstore.entities.*;
import org.example.ebookstore.entities.dtos.UserDto;
import org.example.ebookstore.entities.dtos.UserRegistrationDto;
import org.example.ebookstore.repositories.*;
import org.example.ebookstore.security.CustomUserDetails;
import org.example.ebookstore.services.interfaces.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PictureRepository pictureRepository;
    private Picture defaultPicture;
    private final ModelMapper modelMapper;
    private final CurrencyRepository currencyRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final WishlistRepository wishlistRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PictureRepository pictureRepository, ModelMapper modelMapper, CurrencyRepository currencyRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, ShoppingCartRepository shoppingCartRepository, WishlistRepository wishlistRepository) {
        this.userRepository = userRepository;
        this.pictureRepository = pictureRepository;
        this.modelMapper = modelMapper;
        this.currencyRepository = currencyRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.shoppingCartRepository = shoppingCartRepository;
        this.wishlistRepository = wishlistRepository;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    @Override
    public User save(User user) {
        if (user.getPicture() == null) {
            user.setPicture(this.defaultPicture);
        }
        if (user.getSelectedCurrency() == null) {
            user.setSelectedCurrency(this.currencyRepository.findByCodeIgnoreCase("EUR").get());
        }
        return this.userRepository.save(user);
    }

    @Override
    public Optional<UserDto> getUserDtoByUsername(String username) {
        Optional<User> user = findByUsername(username);
        return user.map(value -> this.modelMapper.map(value, UserDto.class));
    }

    private byte[] loadDefaultImageData() {
        try {
            Resource resource = new ClassPathResource("static/images/default-profile-picture.jpg");
            BufferedImage bImage = ImageIO.read(resource.getInputStream());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bImage, "jpg", bos);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    private void init() {
        this.defaultPicture = this.pictureRepository.findById(1L)
                .orElseGet(() -> {
                    Picture picture = new Picture();
                    picture.setData(loadDefaultImageData());
                    picture.setName("default-profile-picture");
                    picture.setContentType("image/jpg");
                    return this.pictureRepository.save(picture);
                });
    }

    public Currency getSelectedCurrency(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() &&
        !(authentication instanceof AnonymousAuthenticationToken)) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            Optional<UserDto> userDto = getUserDtoByUsername(username);
            if (userDto.isPresent()) {
                return userDto.get().getSelectedCurrency();
            }
        }

        return getCurrencyFromCookie(request);
    }

    @Override
    public void updateUserCurrency(Long userId, Currency currency) {
        Optional<User> optional = this.userRepository.findById(userId);
        if (optional.isPresent()) {
            User user = optional.get();
            user.setSelectedCurrency(currency);
            this.userRepository.save(user);
        }
    }

    @Override
    public Long getUserId(Model model, Authentication authentication) {
        if (!((Boolean) model.getAttribute("isLoggedIn"))) {
            return null;
        }

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        return principal.getId();
    }

    @Override
    public boolean hasUserPurchasedBook(Long userId, Long bookId) {
        return this.userRepository.hasUserPurchasedBook(userId, bookId);
    }

    @Override
    public boolean hasUserRatedBook(Long userId, Long bookId) {
        return this.userRepository.hasUserRatedBook(userId, bookId);
    }

    @Override
    public boolean hasUserReviewedBook(Long userId, Long bookId) {
        return this.userRepository.hasUserReviewedBook(userId, bookId);
    }

    private Currency getCurrencyFromCookie(HttpServletRequest request) {
        String code = "";
        if (request.getCookies() == null) {
            code = "EUR";
        } else {
            code = Arrays.stream(request.getCookies()).filter(c -> "selectedCurrency".equals(c.getName()))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse("EUR");
        }

        return this.currencyRepository.findByCodeIgnoreCase(code).get();
    }

    @Override
    public Optional<User> findById(Long userId) {
        return this.userRepository.findById(userId);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public User createUser(UserRegistrationDto userRegistrationDto) {
        if (this.userRepository.findByEmail(userRegistrationDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("This email is taken. Please choose a different one.");
        }
        if (this.userRepository.findByUsername(userRegistrationDto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("This username is taken. Please choose a different one.");
        }
        if (!userRegistrationDto.getPassword().equals(userRegistrationDto.getConfirmPassword())) {
            throw new IllegalArgumentException("The provided passwords do not match.");
        }

        User user = this.modelMapper.map(userRegistrationDto, User.class);
        Role role = this.roleRepository.findByName(Role.UserRole.USER).get();
        user.addRole(role);
        role.addUser(user);
        this.roleRepository.save(role);
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));

        ShoppingCart shoppingCart = new ShoppingCart();
        Wishlist wishlist = new Wishlist();
        shoppingCart.setUser(user);
        wishlist.setUser(user);

        save(user);

        this.shoppingCartRepository.save(shoppingCart);
        this.wishlistRepository.save(wishlist);

        user.setShoppingCart(shoppingCart);
        user.setWishlist(wishlist);

        return save(user);
    }

    @Override
    public List<UserDto> findAll() {
        return this.userRepository.findAll().stream().map(user -> this.modelMapper.map(user, UserDto.class))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    @Transactional
    public void addAdminRoleToUser(Model model, Long userId) {
        User admin = this.userRepository.findById(((UserDto) model.getAttribute("userDto")).getId())
                .orElseThrow(() -> new IllegalArgumentException("Admin not found."));
        Role role = this.roleRepository.findByName(Role.UserRole.ADMIN).get();
        if (!admin.getRoles().contains(role)) {
            throw new IllegalArgumentException("You are not authorized to change user roles.");
        }
        User user = this.userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found."));

        user.addRole(role);
        role.addUser(user);
        this.roleRepository.save(role);
        this.userRepository.save(user);
    }

    @Override
    @Transactional
    public void removeAdminRoleFromUser(Model model, Long userId) {
        User admin = this.userRepository.findById(((UserDto) model.getAttribute("userDto")).getId())
                .orElseThrow(() -> new IllegalArgumentException("Admin not found."));
        Role role = this.roleRepository.findByName(Role.UserRole.ADMIN).get();
        if (!admin.getRoles().contains(role)) {
            throw new IllegalArgumentException("You are not authorized to change user roles.");
        }
        User user = this.userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found."));

        user.removeRole(role);
        role.removeUser(user);
        this.roleRepository.save(role);
        this.userRepository.save(user);
    }

    public Picture getDefaultPicture() {
        return defaultPicture;
    }
}
