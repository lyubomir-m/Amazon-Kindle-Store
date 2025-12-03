package org.example.ebookstore.services.interfaces;

import org.example.ebookstore.entities.dtos.CategoryDto;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    Optional<CategoryDto> getCategoryDtoById(Long id);
    List<CategoryDto> getDirectSubcategories(Long id);
    List<CategoryDto> getParentCategories(Long id);
}
