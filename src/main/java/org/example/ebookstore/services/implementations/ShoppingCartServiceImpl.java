package org.example.ebookstore.services.implementations;

import jakarta.transaction.Transactional;
import org.example.ebookstore.entities.*;
import org.example.ebookstore.entities.dtos.UserDto;
import org.example.ebookstore.repositories.*;
import org.example.ebookstore.services.interfaces.BookService;
import org.example.ebookstore.services.interfaces.ShoppingCartService;
import org.example.ebookstore.services.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final BookRepository bookRepository;
    private final UserService userService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final BookService bookService;

    @Autowired
    public ShoppingCartServiceImpl(ShoppingCartRepository shoppingCartRepository, BookRepository bookRepository, UserService userService, OrderRepository orderRepository, OrderItemRepository orderItemRepository, ExchangeRateRepository exchangeRateRepository, BookService bookService) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.bookRepository = bookRepository;
        this.userService = userService;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.exchangeRateRepository = exchangeRateRepository;
        this.bookService = bookService;
    }

    @Override
    public int addBookToShoppingCart(Long bookId, Model model) {
        UserDto userDto = (UserDto) model.getAttribute("userDto");
        if (userDto == null) {
            throw new IllegalArgumentException("You have to be logged in.");
        }
        Book book = this.bookRepository.findById(bookId).get();
        ShoppingCart shoppingCart = this.shoppingCartRepository.findById(userDto.getShoppingCart().getId()).get();
        if (shoppingCart.getBooks().contains(book)) {
            throw new IllegalArgumentException("You have already added this book to your shopping cart.");
        }
        if (this.userService.hasUserPurchasedBook(userDto.getId(), bookId)) {
            throw new IllegalArgumentException("You have already purchased this book.");
        }

        shoppingCart.addBook(book);
        book.addShoppingCarts(shoppingCart);
        this.bookRepository.save(book);
        this.shoppingCartRepository.save(shoppingCart);

        return shoppingCart.getBooks().size();
    }

    @Override
    @Transactional
    public void buyAllBooksInShoppingCart(Model model) {
        UserDto userDto = (UserDto) model.getAttribute("userDto");
        if (userDto == null) {
            throw new IllegalArgumentException("You need to be logged in.");
        }

        Long userId = userDto.getId();

        Currency currency = (Currency) model.getAttribute("selectedCurrency");
        User user = this.userService.findById(userId).get();
        ShoppingCart shoppingCart = user.getShoppingCart();
        List<Book> books = shoppingCart.getBooks();
        if (books.isEmpty()) {
            throw new IllegalArgumentException("Your shopping cart is empty.");
        }

        Order order = new Order();
        BigDecimal totalOrderPrice = new BigDecimal("0");
        order.setUser(user);
        order.setCurrency(currency);
        order.setDateTime(LocalDateTime.now());
        order.setExchangeRate(this.exchangeRateRepository.findFirstByCurrencyFromOrderByValidityDateDesc(currency)
                .get().getRate());

        int insertionOrder = 0;
        for (Book book : books) {
            if (this.userService.hasUserPurchasedBook(userId, book.getId())) {
                continue;
            }

            BigDecimal priceInSelectedCurrency = this.bookService.getPriceInSelectedCurrency(book, currency);
            totalOrderPrice = totalOrderPrice.add(priceInSelectedCurrency);

            OrderItem orderItem = new OrderItem();
            orderItem.setBook(book);
            orderItem.setPrice(priceInSelectedCurrency);

            order.addOrderItem(orderItem);
            order.addBook(book);

            book.addOrders(order);
            book.setPurchaseCount(book.getPurchaseCount() + 1);
            orderItem.setOrder(order);
            orderItem.setInsertionOrder(insertionOrder++);
        }

        order.setTotalPrice(this.bookService.round(totalOrderPrice));
        user.addOrder(order);

        this.orderRepository.save(order);
        this.userService.save(user);
        this.orderItemRepository.saveAll(order.getOrderItems());
        this.bookRepository.saveAll(shoppingCart.getBooks());

        shoppingCart.clearBooksInCart();
        this.shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public int removeBookFromShoppingCart(Long bookId, Model model) {
        UserDto userDto = (UserDto) model.getAttribute("userDto");
        if (userDto == null) {
            throw new IllegalArgumentException("You have to be logged in.");
        }
        Book book = this.bookRepository.findById(bookId).get();
        ShoppingCart shoppingCart = this.shoppingCartRepository.findById(userDto.getShoppingCart().getId()).get();
        if (!shoppingCart.getBooks().contains(book)) {
            throw new IllegalArgumentException("The book is not in your shopping cart.");
        }


        shoppingCart.removeBook(book);
        book.removeShoppingCarts(shoppingCart);
        this.bookRepository.save(book);
        this.shoppingCartRepository.save(shoppingCart);

        return shoppingCart.getBooks().size();
    }
}
