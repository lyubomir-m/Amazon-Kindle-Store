package org.example.ebookstore.services.interfaces;

import org.example.ebookstore.entities.dtos.BookDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;

public interface CommonService {
    void addBookPageAttributesToModel(Model model, Page<BookDto> bookDtoPage,
                                      Pageable pageable, int page, String sortBy);
    Sort getSortByParameter(String sortBy);
    void addItemAttributesToModel(Model model, Page<?> dtoPage, Pageable pageable, int page);
}
