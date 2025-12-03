package org.example.ebookstore.services.implementations;

import org.example.ebookstore.entities.Category;
import org.example.ebookstore.entities.dtos.CategoryDto;
import org.example.ebookstore.repositories.CategoryRepository;
import org.example.ebookstore.services.interfaces.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Optional<CategoryDto> getCategoryDtoById(Long id) {
        Optional<Category> optional = this.categoryRepository.findById(id);
        return optional.map(category -> this.modelMapper.map(category, CategoryDto.class));
    }

    @Override
    public List<CategoryDto> getDirectSubcategories(Long id) {
        Optional<Category> optional = this.categoryRepository.findById(id);
        if (optional.isEmpty()) {
            return new ArrayList<>();
        }
        Category category = optional.get();
        return category.getSubcategories().stream().map(c -> this.modelMapper.map(c, CategoryDto.class))
                .sorted(Comparator.comparing(CategoryDto::getId))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<CategoryDto> getParentCategories(Long id) {
        return this.categoryRepository.findAllParentCategories(id).stream()
                .map(c -> this.modelMapper.map(c, CategoryDto.class))
                .collect(Collectors.toCollection(ArrayList::new));
    }

}
