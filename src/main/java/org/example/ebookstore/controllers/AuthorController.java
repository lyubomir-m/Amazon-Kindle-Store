package org.example.ebookstore.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.example.ebookstore.entities.Currency;
import org.example.ebookstore.entities.dtos.AuthorDto;
import org.example.ebookstore.entities.dtos.BookDto;
import org.example.ebookstore.services.interfaces.*;
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

import java.util.Optional;

@Controller
public class AuthorController {
    private final BookService bookService;
    private final UserService userService;
    private final AuthorService authorService;
    private final CommonService commonService;

    @Autowired
    public AuthorController(BookService bookService, UserService userService, AuthorService authorService, CommonService commonService) {
        this.bookService = bookService;
        this.userService = userService;
        this.authorService = authorService;
        this.commonService = commonService;
    }

    @GetMapping("/authors/{id}")
    public String viewAuthorPage(@PathVariable("id") Long id,
                                 @RequestParam(defaultValue = "0") int page,
                                 Model model, HttpServletRequest request,
                                 @RequestParam(defaultValue = "publicationDateDesc") String sortBy) {
        Optional<AuthorDto> optional = this.authorService.findById(id);
        if (optional.isEmpty()) {
            return "error";
        }

        AuthorDto author = optional.get();
        model.addAttribute("author", author);

        Sort sort = this.commonService.getSortByParameter(sortBy);
        Pageable pageable = PageRequest.of(page, 16, sort);
        Currency currency = this.userService.getSelectedCurrency(request);
        Page<BookDto> bookDtoPage = this.bookService.findByAuthorId(id, pageable, currency);

        this.commonService.addBookPageAttributesToModel(model, bookDtoPage, pageable, page, sortBy);


        return "author";
    }
}
