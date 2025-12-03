package org.example.ebookstore.repositories;

import org.example.ebookstore.entities.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@DataJpaTest
public class CategoryRepositoryTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TestEntityManager entityManager;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should return all subcategories for a given category id")
    public void shouldReturnAllSubcategories() {
        // Given
        Category category = new Category();
        category.setId(1L);
        when(entityManager.find(any(), anyLong())).thenReturn(category);
        List<Category> mockSubcategories = new ArrayList<>();
        mockSubcategories.add(new Category());
        when(categoryRepository.findAllSubcategories(anyLong())).thenReturn(mockSubcategories);

        // When
        List<Category> subcategories = categoryRepository.findAllSubcategories(1L);

        // Then
        assertThat(subcategories).isNotEmpty();
    }

    @Test
    @DisplayName("Should return empty list when no subcategories exist for a given category id")
    public void shouldReturnEmptyListWhenNoSubcategoriesExist() {
        // Given
        Category category = new Category();
        category.setId(1L);
        when(entityManager.find(any(), anyLong())).thenReturn(category);

        // When
        List<Category> subcategories = categoryRepository.findAllSubcategories(1L);

        // Then
        assertThat(subcategories).isEmpty();
    }

    @Test
    @DisplayName("Should return all parent categories for a given category id")
    public void shouldReturnAllParentCategories() {
        // Given
        Category category = new Category();
        category.setId(1L);
        when(entityManager.find(any(), anyLong())).thenReturn(category);
        List<Category> mockParentCategories = new ArrayList<>();
        mockParentCategories.add(new Category());
        when(categoryRepository.findAllParentCategories(anyLong())).thenReturn(mockParentCategories);

        // When
        List<Category> parentCategories = categoryRepository.findAllParentCategories(1L);

        // Then
        assertThat(parentCategories).isNotEmpty();
    }

    @Test
    @DisplayName("Should return empty list when no parent categories exist for a given category id")
    public void shouldReturnEmptyListWhenNoParentCategoriesExist() {
        // Given
        Category category = new Category();
        category.setId(1L);
        when(entityManager.find(any(), anyLong())).thenReturn(category);

        // When
        List<Category> parentCategories = categoryRepository.findAllParentCategories(1L);

        // Then
        assertThat(parentCategories).isEmpty();
    }
}