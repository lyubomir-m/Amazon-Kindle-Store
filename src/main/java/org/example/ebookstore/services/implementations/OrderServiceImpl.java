package org.example.ebookstore.services.implementations;

import org.example.ebookstore.entities.*;
import org.example.ebookstore.entities.dtos.OrderDto;
import org.example.ebookstore.entities.dtos.UserDto;
import org.example.ebookstore.repositories.*;
import org.example.ebookstore.services.interfaces.BookService;
import org.example.ebookstore.services.interfaces.OrderService;
import org.example.ebookstore.services.interfaces.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final BookService bookService;
    private final OrderItemRepository orderItemRepository;
    private final BookRepository bookRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final ModelMapper modelMapper;
    private final ShoppingCartRepository shoppingCartRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, UserService userService, BookService bookService, OrderItemRepository orderItemRepository, BookRepository bookRepository, ExchangeRateRepository exchangeRateRepository, ModelMapper modelMapper, ShoppingCartRepository shoppingCartRepository) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.bookService = bookService;
        this.orderItemRepository = orderItemRepository;
        this.bookRepository = bookRepository;
        this.exchangeRateRepository = exchangeRateRepository;
        this.modelMapper = modelMapper;
        this.shoppingCartRepository = shoppingCartRepository;
    }

    @Override
    public Order createOrder(Long bookId, Model model) {
        UserDto userDto = (UserDto) model.getAttribute("userDto");
        if (userDto == null) {
            throw new IllegalArgumentException("You need to be logged in.");
        }

        Long userId = userDto.getId();
        if (this.userService.hasUserPurchasedBook(userId, bookId)) {
            throw new IllegalArgumentException("You have already purchased this book.");
        }

        Currency currency = (Currency) model.getAttribute("selectedCurrency");
        User user = this.userService.findById(userId).get();
        Book book = this.bookRepository.findById(bookId).get();
        BigDecimal priceInSelectedCurrency = this.bookService.getPriceInSelectedCurrency(book, currency);

        OrderItem orderItem = new OrderItem();
        orderItem.setBook(book);
        orderItem.setPrice(priceInSelectedCurrency);
        orderItem.setInsertionOrder(0);

        Order order = new Order();
        order.addOrderItem(orderItem);
        order.addBook(book);
        order.setUser(user);
        order.setCurrency(currency);
        order.setDateTime(LocalDateTime.now());
        order.setExchangeRate(this.exchangeRateRepository.findFirstByCurrencyFromOrderByValidityDateDesc(currency)
                .get().getRate());
        order.setTotalPrice(priceInSelectedCurrency);

        book.addOrders(order);
        book.setPurchaseCount(book.getPurchaseCount() + 1);
        orderItem.setOrder(order);

        user.addOrder(order);
        ShoppingCart shoppingCart = user.getShoppingCart();
        if (shoppingCart.getBooks().contains(book)) {
            shoppingCart.removeBook(book);
            this.shoppingCartRepository.save(shoppingCart);
        }

        order = this.orderRepository.save(order);
        this.userService.save(user);
        this.orderItemRepository.save(orderItem);
        this.bookRepository.save(book);
        return order;
    }

    @Override
    public Page<OrderDto> findByUserId(Long userId, Pageable pageable) {
        return this.orderRepository.findByUserIdOrderByDateTimeDesc(userId, pageable)
                .map(order -> this.modelMapper.map(order, OrderDto.class));
    }
}
