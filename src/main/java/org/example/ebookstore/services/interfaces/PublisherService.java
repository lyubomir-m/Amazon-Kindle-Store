package org.example.ebookstore.services.interfaces;

import org.example.ebookstore.entities.Publisher;
import org.example.ebookstore.entities.dtos.PublisherDto;

import java.util.Optional;

public interface PublisherService {
    Publisher save(Publisher publisher);
    Optional<PublisherDto> findById(Long id);
}
