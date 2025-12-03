package org.example.ebookstore.config;

import org.example.ebookstore.entities.Author;
import org.example.ebookstore.entities.Picture;
import org.example.ebookstore.entities.Publisher;
import org.example.ebookstore.entities.User;
import org.example.ebookstore.entities.dtos.AuthorDto;
import org.example.ebookstore.entities.dtos.PublisherDto;
import org.example.ebookstore.entities.dtos.UserDto;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.spi.MappingContext;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Configuration
@EnableScheduling
@EnableAspectJAutoProxy
@EnableCaching
public class AppConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        Converter<byte[], String> toBase64String = ctx -> Base64.getEncoder().encodeToString(ctx.getSource());

        Converter<Picture, String> pictureToBase64WithContentType = ctx -> {
            if (ctx.getSource() == null) {
                return null; //
            }
            Picture picture = ctx.getSource();
            String contentType = picture.getContentType();
            byte[] data = picture.getData();
            String base64Data = Base64.getEncoder().encodeToString(data);
            return String.format("data:%s;base64,%s", contentType, base64Data);
        };

        modelMapper.createTypeMap(User.class, UserDto.class).addMappings(mapper -> {
            mapper.using(pictureToBase64WithContentType).map(User::getPicture, UserDto::setPictureBase64);
        });

        modelMapper.createTypeMap(Author.class, AuthorDto.class).addMappings(mapper -> {
            mapper.using(pictureToBase64WithContentType).map(Author::getPicture, AuthorDto::setPictureBase64);
        });

        return modelMapper;
    }
}
