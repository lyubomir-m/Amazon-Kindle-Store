package org.example.ebookstore.services.implementations;

import jakarta.transaction.Transactional;
import org.example.ebookstore.entities.Book;
import org.example.ebookstore.entities.Rating;
import org.example.ebookstore.entities.Review;
import org.example.ebookstore.entities.User;
import org.example.ebookstore.entities.dtos.ReviewDto;
import org.example.ebookstore.entities.dtos.ReviewResultDto;
import org.example.ebookstore.entities.dtos.ReviewSubmissionDto;
import org.example.ebookstore.entities.dtos.UserDto;
import org.example.ebookstore.repositories.BookRepository;
import org.example.ebookstore.repositories.RatingRepository;
import org.example.ebookstore.repositories.ReviewRepository;
import org.example.ebookstore.repositories.UserRepository;
import org.example.ebookstore.services.interfaces.PlaceholderReviewService;
import org.example.ebookstore.services.interfaces.RatingService;
import org.example.ebookstore.services.interfaces.ReviewService;
import org.example.ebookstore.services.interfaces.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;
    private final BookRepository bookRepository;
    private final UserService userService;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository, ModelMapper modelMapper, UserRepository userRepository, RatingRepository ratingRepository, BookRepository bookRepository, UserService userService) {
        this.reviewRepository = reviewRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
        this.bookRepository = bookRepository;
        this.userService = userService;
    }

    @Override
    @Transactional
    public Page<ReviewDto> getReviewsByBookId(Long bookId, Pageable pageable) {
        return this.reviewRepository.findAllByBookId(bookId, pageable)
                .map(review -> this.modelMapper.map(review, ReviewDto.class));
    }

    @Override
    @Transactional
    public ReviewResultDto createReview(ReviewSubmissionDto reviewSubmissionDto, Model model, Long bookId) {
        checkReviewCreation(model, bookId);
        UserDto userDto = (UserDto) model.getAttribute("userDto");
        Long userId = userDto.getId();

        User user = this.userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found."));
        Book book = this.bookRepository.findById(bookId).orElseThrow(() -> new IllegalArgumentException("Book not found."));
        Rating rating = null;
        if (this.userService.hasUserRatedBook(userId, bookId)) {
            rating = this.ratingRepository.findByUserIdAndBookId(userId, bookId).orElseThrow(() ->
                    new IllegalArgumentException("Rating not found."));
            book.removeRating(rating);
            rating.setRatingValue(reviewSubmissionDto.getReviewRating());
            rating.setSubmissionDate(LocalDate.now());
        } else {
            rating = new Rating(user, LocalDate.now(), book, reviewSubmissionDto.getReviewRating());
        }

        Review review = new Review(user, reviewSubmissionDto.getReviewTitle(), reviewSubmissionDto.getReviewText(),
                LocalDate.now(), rating, book);
        book.addRating(rating);
        book.addReviews(review);
        this.bookRepository.save(book);
        this.ratingRepository.save(rating);
        this.reviewRepository.save(review);

        ReviewDto reviewDto = this.modelMapper.map(review, ReviewDto.class);
        ReviewResultDto reviewResultDto = new ReviewResultDto(book.getAverageRating(), book.getRatingsCount(),
                userDto.getPictureBase64(), userDto.getFirstName(), userDto.getLastName(),
                rating.getRatingValue(), reviewDto.getTitle(), reviewDto.getText(), reviewDto.getSubmissionDate());
        return reviewResultDto;
    }

    @Override
    public void checkReviewCreation(Model model, Long bookId) {
        UserDto userDto = (UserDto) model.getAttribute("userDto");
        if (userDto == null) {
            throw new IllegalArgumentException("You have to be logged in.");
        }
        Long userId = userDto.getId();
        if (!this.userService.hasUserPurchasedBook(userId, bookId)) {
            throw new IllegalArgumentException("You can only review books that you have purchased.");
        }
        if (this.userService.hasUserReviewedBook(userId, bookId)) {
            throw new IllegalArgumentException("You have already reviewed this book.");
        }
    }

    @Override
    @Transactional
    public String deleteReview(Model model, Long bookId) {
        UserDto userDto = (UserDto) model.getAttribute("userDto");
        if (userDto == null) {
            throw new IllegalArgumentException("You have to be logged in.");
        }
        Long userId = userDto.getId();

        Review review = this.reviewRepository.findByUserIdAndBookId(userId, bookId).orElseThrow(() ->
                new IllegalArgumentException("Review not found."));
        Book book = review.getBook();
        Rating rating = review.getRating();

        book.removeReviews(review);
        book.removeRating(rating);

        this.bookRepository.save(book);
        this.reviewRepository.delete(review);
        this.ratingRepository.delete(rating);

        return "The review and the rating associated with it were deleted.";
    }

    @Override
    @Transactional
    public Page<ReviewDto> findByUserId(Long userId, Pageable pageable) {
        return this.reviewRepository.findByUserIdOrderBySubmissionDateDesc(userId, pageable)
                .map(review -> this.modelMapper.map(review, ReviewDto.class));
    }

    @Override
    @Transactional
    public void updateReview(ReviewSubmissionDto reviewSubmissionDto, Model model, Long bookId) {
        UserDto userDto = (UserDto) model.getAttribute("userDto");
        if (userDto == null) {
            throw new IllegalArgumentException("User not found.");
        }
        Long userId = userDto.getId();

        User user = this.userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found."));
        Book book = this.bookRepository.findById(bookId).orElseThrow(() -> new IllegalArgumentException("Book not found."));
        Rating rating = null;
        if (this.userService.hasUserRatedBook(userId, bookId)) {
            rating = this.ratingRepository.findByUserIdAndBookId(userId, bookId).orElseThrow(() ->
                    new IllegalArgumentException("Rating not found."));
            book.removeRating(rating);
            rating.setRatingValue(reviewSubmissionDto.getReviewRating());
            rating.setSubmissionDate(LocalDate.now());
        } else {
            throw new IllegalArgumentException("Rating not found.");
        }

        Review review = this.reviewRepository.findByUserIdAndBookId(user.getId(), book.getId())
                .orElseThrow(() -> new IllegalArgumentException("Review not found."));

        review.setRating(rating);
        review.setTitle(reviewSubmissionDto.getReviewTitle());
        review.setText(reviewSubmissionDto.getReviewText());
        review.setSubmissionDate(LocalDate.now());

        book.addRating(rating);
        this.bookRepository.save(book);
        this.ratingRepository.save(rating);
        this.reviewRepository.save(review);
    }

    @Override
    @Transactional
    public List<ReviewDto> getPlaceholderReviews(int count) {
        List<Review> allPlaceholders = this.reviewRepository.findFirst10ByOrderByIdAsc();
        Collections.shuffle(allPlaceholders);
        return getSubList(allPlaceholders, count);
    }

    @Transactional
    public List<ReviewDto> getSubList(List<Review> list, int count) {
        Book book = this.bookRepository.findById(1L).get();
        return list.subList(0, count).stream().peek(review -> {
                    // Force initialization of the necessary properties
                    review.setBook(book);
                    review.getBook().getReviews().size();
                }).map(review -> this.modelMapper.map(review, ReviewDto.class))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
