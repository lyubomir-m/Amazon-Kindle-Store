package org.example.ebookstore.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.example.ebookstore.entities.Currency;
import org.example.ebookstore.entities.dtos.BookDto;
import org.example.ebookstore.entities.dtos.PublisherDto;
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
public class PublisherController {
    private final BookService bookService;
    private final UserService userService;
    private final CommonService commonService;
    private final PublisherService publisherService;

    @Autowired
    public PublisherController(BookService bookService, UserService userService, CommonService commonService, PublisherService publisherService) {
        this.bookService = bookService;
        this.userService = userService;
        this.commonService = commonService;
        this.publisherService = publisherService;
    }

    @GetMapping("/publishers/{id}")
    public String viewPublisherPage(@PathVariable("id") Long id,
                                    @RequestParam(defaultValue = "0") int page,
                                    Model model, HttpServletRequest request,
                                    @RequestParam(defaultValue = "publicationDateDesc") String sortBy) {
        Optional<PublisherDto> optional = this.publisherService.findById(id);
        if (optional.isEmpty()) {
            return "error";
        }

        PublisherDto publisher = optional.get();
        model.addAttribute("publisher", publisher);

        Sort sort = this.commonService.getSortByParameter(sortBy);
        Pageable pageable = PageRequest.of(page, 16, sort);
        Currency currency = this.userService.getSelectedCurrency(request);
        Page<BookDto> bookDtoPage = this.bookService.findByPublisherId(id, pageable, currency);

        this.commonService.addBookPageAttributesToModel(model, bookDtoPage, pageable, page, sortBy);
        return "publisher";
    }
}
