package org.example.ebookstore.repositories;

import org.example.ebookstore.entities.Book;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @Cacheable("homepageBooks")
    List<Book> findFirst54ByAverageRatingGreaterThanEqualOrderByPurchaseCountDesc(Double avgRating);
    Page<Book> findByCategoriesIdIn(List<Long> categoryIds, Pageable pageable);

    @Query("select b from Book b join b.categories c where c.id = :categoryId and " +
            "b.id != :bookId order by b.purchaseCount desc")
    Page<Book> getRecommendedBooks(@Param("bookId") Long bookId, @Param("categoryId") Long categoryId,
                                   Pageable pageable);
    List<Book> findFirst50000ByOrderByPurchaseCountDesc();
    Page<Book> findByAuthorsId(Long authorId, Pageable pageable);
    Page<Book> findByPublisherId(Long publisherId, Pageable pageable);
    @Query("select b from Book b join b.orders o where o.user.id = :userId " +
            "order by o.dateTime desc")
    Page<Book> findByUserId(@Param("userId") Long userId, Pageable pageable);
    Page<Book> findByShoppingCartsId(Long cartId, Pageable pageable);
    Page<Book> findByWishlistsId(Long wishlistId, Pageable pageable);
    @Query("select b from Book b join b.authors a join b.publisher p " +
            "where b.title like %:keywords% or a.fullName like %:keywords% or p.name like %:keywords%")
    Page<Book> findBySearchQuery(@Param("keywords") String keywords, Pageable pageable);
    Page<Book> findAllByIdInOrAuthorsIdInOrPublisherIdIn(List<Long> bookIds, List<Long> authorIds,
                                                                     List<Long> publisherIds, Pageable pageable);
    List<Book> findAllByTitleLike(String keywords);
    Page<Book> findAllBySearchColumnLike(String keywords, Pageable pageable);
    @Query("select b from Book b where b.searchColumn like %:keywords%")
    Page<Book> findAllBySearchQuery2(@Param("keywords") String keywords, Pageable pageable);
}
