package org.example.ebookstore.services.implementations;

import org.example.ebookstore.entities.*;
import org.example.ebookstore.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;

public class BaseServiceTest {
    protected User user;
    protected Book book1;
    protected Book book2;
    protected Author author;
    protected Role adminRole;
    protected Publisher publisher;
    protected ExchangeRate exchangeRate;
    protected Currency currency;
    protected Currency currency2;

    protected final UserRepository userRepository;
    protected final BookRepository bookRepository;
    protected final AuthorRepository authorRepository;
    protected final RoleRepository roleRepository;
    protected final PublisherRepository publisherRepository;
    protected final ExchangeRateRepository exchangeRateRepository;
    protected final CurrencyRepository currencyRepository;

    public BaseServiceTest(UserRepository userRepository, BookRepository bookRepository, AuthorRepository authorRepository, RoleRepository roleRepository, PublisherRepository publisherRepository, ExchangeRateRepository exchangeRateRepository, CurrencyRepository currencyRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.roleRepository = roleRepository;
        this.publisherRepository = publisherRepository;
        this.exchangeRateRepository = exchangeRateRepository;
        this.currencyRepository = currencyRepository;
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        user = this.userRepository.findById(3L).orElseThrow(() -> new IllegalArgumentException("Object not found."));
        book1 = this.bookRepository.findById(1L).orElseThrow(() -> new IllegalArgumentException("Object not found."));
        book2 = this.bookRepository.findById(2L).orElseThrow(() -> new IllegalArgumentException("Object not found."));
        author = this.authorRepository.findById(1L).orElseThrow(() -> new IllegalArgumentException("Object not found."));
        adminRole = this.roleRepository.findById(1L).orElseThrow(() -> new IllegalArgumentException("Object not found."));
        publisher = this.publisherRepository.findById(1L).orElseThrow(() -> new IllegalArgumentException("Object not found."));
        exchangeRate = this.exchangeRateRepository.findById(1L).orElseThrow(() -> new IllegalArgumentException("Object not found."));
        currency = this.currencyRepository.findById(1L).orElseThrow(() -> new IllegalArgumentException("Object not found."));
        currency2 = this.currencyRepository.findById(2L).orElseThrow(() -> new IllegalArgumentException("Object not found."));
    }
}