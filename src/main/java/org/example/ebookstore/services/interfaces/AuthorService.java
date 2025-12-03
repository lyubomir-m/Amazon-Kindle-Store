package org.example.ebookstore.services.interfaces;

import org.example.ebookstore.entities.Author;
import org.example.ebookstore.entities.Currency;
import org.example.ebookstore.entities.dtos.AuthorDto;

import java.util.Optional;

public interface AuthorService {
    Author save(Author author);
    Optional<AuthorDto> findById(Long authorId);
}
