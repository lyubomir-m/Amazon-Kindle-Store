package org.example.ebookstore.services.implementations;

import org.example.ebookstore.entities.*;
import org.example.ebookstore.entities.Currency;
import org.example.ebookstore.entities.dtos.BookDto;
import org.example.ebookstore.repositories.*;
import org.example.ebookstore.services.interfaces.BookService;
import org.example.ebookstore.services.interfaces.ExchangeRateService;
import org.example.ebookstore.services.interfaces.PlaceholderReviewService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final ExchangeRateService exchangeRateService;
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final CurrencyRepository currencyRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, ExchangeRateService exchangeRateService, ModelMapper modelMapper, CategoryRepository categoryRepository, ExchangeRateRepository exchangeRateRepository, CurrencyRepository currencyRepository, AuthorRepository authorRepository, PublisherRepository publisherRepository) {
        this.bookRepository = bookRepository;
        this.exchangeRateService = exchangeRateService;
        this.modelMapper = modelMapper;
        this.categoryRepository = categoryRepository;
        this.exchangeRateRepository = exchangeRateRepository;
        this.currencyRepository = currencyRepository;
        this.authorRepository = authorRepository;
        this.publisherRepository = publisherRepository;
    }

    public BigDecimal round(BigDecimal value) {


        return value.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getPriceInSelectedCurrency(Book book, Currency currency) {
        BigDecimal price = null;
        String code = currency.getCode();
        if (code.equals("EUR")) {
            price = book.getPriceEur();
        } else if (code.equals("USD")) {
            price = book.getPriceUsd();
        } else if (code.equals("AUD")) {
            price = book.getPriceAud();
        } else if (code.equals("BRL")) {
            price = book.getPriceBrl();
        } else if (code.equals("INR")) {
            price = book.getPriceInr();
        } else if (code.equals("CNY")) {
            price = book.getPriceCny();
        } else if (code.equals("EGP")) {
            price = book.getPriceEgp();
        } else if (code.equals("NGN")) {
            price = book.getPriceNgn();
        } else {
            price = book.getPriceEur();
        }

        return round(price);
    }

    @Override
    public BookDto mapBookToDto(Book book, Currency currency) {
        BigDecimal price = getPriceInSelectedCurrency(book, currency);
        BookDto bookDto = this.modelMapper.map(book, BookDto.class);
        bookDto.setSelectedCurrency(currency);
        bookDto.setSelectedCurrencyPrice(price);
        double rating = book.getAverageRating();

        int fullStars = (int) rating;
        int halfStar = (rating - fullStars >= 0.5) ? 1 : 0;
        int emptyStars = 5 - (fullStars + halfStar);

        bookDto.setFullStars(fullStars);
        bookDto.setHalfStar(halfStar);
        bookDto.setEmptyStars(emptyStars);

        return bookDto;
    }

    private List<Long> getCategoryAndSubcategoryIds(Long categoryId) {
        List<Long> subcategoryIds = this.categoryRepository.findAllSubcategories(categoryId)
                .stream().map(BaseEntity::getId).collect(Collectors.toCollection(ArrayList::new));
        subcategoryIds.add(categoryId);
        return subcategoryIds;
    }

    @Override
    public Page<BookDto> findByCategoryId(Long categoryId, Pageable pageable, Currency currency) {
        List<Long> categoryIds = getCategoryAndSubcategoryIds(categoryId);
        Page<Book> books = this.bookRepository.findByCategoriesIdIn(categoryIds, pageable);
        return books.map(book -> mapBookToDto(book, currency));
    }

    @Override
    @Cacheable("homepageBookDtos")
    public List<BookDto> findFirst54BestSellers(Currency currency) {
        return this.bookRepository.findFirst54ByAverageRatingGreaterThanEqualOrderByPurchaseCountDesc(0.1)
                .stream().map(book -> mapBookToDto(book, currency))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<BookDto> getDto(Long id, Currency currency) {
        Optional<Book> optional = this.bookRepository.findById(id);
        return optional.map(book -> mapBookToDto(book, currency));
    }

    @Override
    public List<BookDto> getRecommendedBooks(Long id, Currency currency) {
        Book book = this.bookRepository.findById(id).get();
        Long categoryId = book.getCategories().stream().filter(c -> !c.getId().equals(1L))
                .findFirst().map(Category::getId).get();
        Pageable pageable = PageRequest.of(0, 6, Sort.by("purchaseCount").descending());
        Page<Book> page = this.bookRepository.getRecommendedBooks(id, categoryId, pageable);
        return page.getContent().stream().map(b -> mapBookToDto(b, currency))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Page<BookDto> findByAuthorId(Long authorId, Pageable pageable, Currency currency) {
        Page<Book> books = this.bookRepository.findByAuthorsId(authorId, pageable);
        return books.map(book -> mapBookToDto(book, currency));
    }

    @Override
    public Page<BookDto> findByPublisherId(Long publisherId, Pageable pageable, Currency currency) {
        return this.bookRepository.findByPublisherId(publisherId, pageable)
                .map(book -> mapBookToDto(book, currency));
    }

    @Override
    public Page<BookDto> findByUserId(Long userId, Pageable pageable, Currency currency) {
        return this.bookRepository.findByUserId(userId, pageable)
                .map(book -> mapBookToDto(book, currency));
    }

    @Override
    public Page<BookDto> findByShoppingCartId(Long cartId, Pageable pageable, Currency currency) {
        return this.bookRepository.findByShoppingCartsId(cartId, pageable)
                .map(book -> mapBookToDto(book, currency));
    }

    @Override
    public Page<BookDto> findByWishlistId(Long wishlistId, Pageable pageable, Currency currency) {
        return this.bookRepository.findByWishlistsId(wishlistId, pageable)
                .map(book -> mapBookToDto(book, currency));
    }

    @Override
    public void updateFxPricesOfAllBooks() {
        BigDecimal usdRate = this.exchangeRateService.getLatestRate("USD").get();
        BigDecimal audRate = this.exchangeRateService.getLatestRate("AUD").get();
        BigDecimal brlRate = this.exchangeRateService.getLatestRate("BRL").get();
        BigDecimal inrRate = this.exchangeRateService.getLatestRate("INR").get();
        BigDecimal cnyRate = this.exchangeRateService.getLatestRate("CNY").get();
        BigDecimal egpRate = this.exchangeRateService.getLatestRate("EGP").get();
        BigDecimal ngnRate = this.exchangeRateService.getLatestRate("NGN").get();
        List<Book> books = this.bookRepository.findAll();

        for (Book book : books) {
            BigDecimal priceEur = book.getPriceEur();
            book.setPriceUsd(round(priceEur.multiply(usdRate)));
            book.setPriceAud(round(priceEur.multiply(audRate)));
            book.setPriceBrl(round(priceEur.multiply(brlRate)));
            book.setPriceInr(round(priceEur.multiply(inrRate)));
            book.setPriceCny(round(priceEur.multiply(cnyRate)));
            book.setPriceEgp(round(priceEur.multiply(egpRate)));
            book.setPriceNgn(round(priceEur.multiply(ngnRate)));
        }

        this.bookRepository.saveAll(books);
    }

    @Override
    public Page<BookDto> findBySearchQuery(String query, Pageable pageable, Currency currency) {
//        List<Long> bookIds = this.bookRepository.findAllByTitleLike(query)
//                .stream().map(Book::getId).collect(Collectors.toCollection(ArrayList::new));
//        List<Long> authorIds = this.authorRepository.findAllByFullNameLike(query)
//                .stream().map(Author::getId).collect(Collectors.toCollection(ArrayList::new));
//        List<Long> publisherIds = this.publisherRepository.findAllByNameLike(query)
//                .stream().map(Publisher::getId).collect(Collectors.toCollection(ArrayList::new));
//
//        System.out.println("Book ids: ");
//        for (Long id : bookIds) {
//            System.out.print(id + ", ");
//        }
//        System.out.println("%nAuthor ids: ");
//        authorIds.forEach(id -> System.out.print(id + ", "));
//        System.out.println("%nPublisher ids: ");
//        publisherIds.forEach(id -> System.out.print(id + ", "));
//
//        return this.bookRepository.findAllByIdInOrAuthorsIdInOrPublisherIdIn(bookIds, authorIds,
//                publisherIds, pageable).map(book -> mapBookToDto(book, currency));

//        return this.bookRepository.findBySearchQuery(query, pageable)
//                .map(book -> mapBookToDto(book, currency));

//        return this.bookRepository.findAllBySearchColumnLike(query, pageable)
//                .map(book -> mapBookToDto(book, currency));

        return this.bookRepository.findAllBySearchQuery2(query, pageable)
                .map(book -> mapBookToDto(book, currency));
    }
}

