package org.example.ebookstore.repositories;

import org.example.ebookstore.entities.Book;
import org.example.ebookstore.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query(value = "with recursive subcategories as (" +
            "select id, name, parent_id " +
            "from categories where id = :categoryId " +
            "union all " +
            "select c.id, c.name, c.parent_id " +
            "from subcategories s join categories c on s.id = c.parent_id) " +
            "select * from subcategories s where s.id != :categoryId", nativeQuery = true)
    List<Category> findAllSubcategories(@Param("categoryId") Long categoryId);

    @Query(value = "with recursive parent_categories as (" +
            "select id, name, parent_id " +
            "from categories where id = :categoryId " +
            "union all " +
            "select c.id, c.name, c.parent_id " +
            "from categories c " +
            "inner join parent_categories pc on pc.parent_id = c.id) " +
            "select * from parent_categories where id != :categoryId order by id asc", nativeQuery = true)
    List<Category> findAllParentCategories(@Param("categoryId") Long categoryId);
}
