package ru.otus.hw.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.model.Author;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthorMapper {
    AuthorDto authorToAuthorDto(final Author author);

    List<AuthorDto> authorsToAuthorsDto(final List<Author> author);
}
