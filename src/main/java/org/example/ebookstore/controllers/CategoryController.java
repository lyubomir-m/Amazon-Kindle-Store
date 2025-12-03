package org.example.ebookstore.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.example.ebookstore.entities.Currency;
import org.example.ebookstore.entities.dtos.BookDto;
import org.example.ebookstore.entities.dtos.CategoryDto;
import org.example.ebookstore.services.interfaces.BookService;
import org.example.ebookstore.services.interfaces.CategoryService;
import org.example.ebookstore.services.interfaces.CommonService;
import org.example.ebookstore.services.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class CategoryController {
    private final BookService bookService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final CommonService commonService;

    @Autowired
    public CategoryController(BookService bookService, UserService userService, CategoryService categoryService, CommonService commonService) {
        this.bookService = bookService;
        this.userService = userService;
        this.categoryService = categoryService;
        this.commonService = commonService;
    }

    @GetMapping("/categories/{id}")
    public String viewCategoryPage(@PathVariable("id") Long id,
                                   @RequestParam(defaultValue = "0") int page,
                                   Model model, HttpServletRequest request,
                                   @RequestParam(defaultValue = "purchaseCountDesc") String sortBy) {
        Optional<CategoryDto> optional = this.categoryService.getCategoryDtoById(id);
        if (optional.isEmpty()) {
            return "error";
        }

        CategoryDto currentCategory = optional.get();
        List<CategoryDto> directSubcategories = this.categoryService.getDirectSubcategories(id);
        List<CategoryDto> parentCategories = this.categoryService.getParentCategories(id);
        model.addAttribute("currentCategory", currentCategory);
        model.addAttribute("directSubcategories", directSubcategories);
        model.addAttribute("parentCategories", parentCategories);

        Currency currency = this.userService.getSelectedCurrency(request);
        Sort sort = this.commonService.getSortByParameter(sortBy);
        Pageable pageable = PageRequest.of(page, 16, sort);
        Page<BookDto> bookDtoPage = this.bookService.findByCategoryId(id, pageable, currency);

        this.commonService.addBookPageAttributesToModel(model, bookDtoPage, pageable, page, sortBy);
        return "category";
    }
}
