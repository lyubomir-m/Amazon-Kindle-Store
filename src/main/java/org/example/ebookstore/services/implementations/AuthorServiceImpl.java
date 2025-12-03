package org.example.ebookstore.services.implementations;

import jakarta.annotation.PostConstruct;
import org.example.ebookstore.entities.Author;
import org.example.ebookstore.entities.Currency;
import org.example.ebookstore.entities.Picture;
import org.example.ebookstore.entities.dtos.AuthorDto;
import org.example.ebookstore.repositories.AuthorRepository;
import org.example.ebookstore.repositories.PictureRepository;
import org.example.ebookstore.services.interfaces.AuthorService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;
    private final PictureRepository pictureRepository;
    private Picture defaultPicture;
    private final ModelMapper modelMapper;

    @Autowired
    public AuthorServiceImpl(AuthorRepository authorRepository, PictureRepository pictureRepository, ModelMapper modelMapper) {
        this.authorRepository = authorRepository;
        this.pictureRepository = pictureRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Author save(Author author) {
        if (author.getPicture() == null) {
            author.setPicture(defaultPicture);
        }
        return this.authorRepository.save(author);
    }

    @Override
    public Optional<AuthorDto> findById(Long authorId) {
        return this.authorRepository.findById(authorId).map(a -> this.modelMapper.map(a, AuthorDto.class));
    }

    private byte[] loadDefaultImageData() {
        try {
            Resource resource = new ClassPathResource("static/images/default-profile-picture.jpg");
            BufferedImage bImage = ImageIO.read(resource.getInputStream());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bImage, "jpg", bos);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    public void init() {
        this.defaultPicture = this.pictureRepository.findById(1L)
                .orElseGet(() -> {
                    Picture picture = new Picture();
                    picture.setData(loadDefaultImageData());
                    picture.setName("default-profile-picture");
                    picture.setContentType("image/jpg");
                    return this.pictureRepository.save(picture);
                });
    }
}
