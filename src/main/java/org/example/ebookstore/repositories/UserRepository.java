package org.example.ebookstore.repositories;

import org.example.ebookstore.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findFirst10ByOrderByIdAsc();
    @Query("select exists (select 1 from Order o join o.books b " +
            "where o.user.id = :userId and b.id = :bookId)")
    boolean hasUserPurchasedBook(@Param("userId") Long userId, @Param("bookId") Long bookId);
    @Query("select exists (select 1 from Rating r where r.book.id = :bookId and r.user.id = :userId)")
    boolean hasUserRatedBook(@Param("userId") Long userId, @Param("bookId") Long bookId);
    @Query("select exists (select 1 from Review r where r.user.id = :userId and r.book.id = :bookId)")
    boolean hasUserReviewedBook(@Param("userId") Long userId, @Param("bookId") Long bookId);
}
