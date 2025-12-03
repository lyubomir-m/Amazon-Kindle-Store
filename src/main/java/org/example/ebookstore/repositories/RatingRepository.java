package org.example.ebookstore.repositories;

import org.example.ebookstore.entities.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByUserIdAndBookId(Long userId, Long bookId);
    Page<Rating> findByUserIdOrderBySubmissionDateDesc(Long userId, Pageable pageable);
}
