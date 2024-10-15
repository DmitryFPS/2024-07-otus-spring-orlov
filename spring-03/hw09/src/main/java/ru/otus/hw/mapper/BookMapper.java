package ru.otus.hw.mapper;

import org.mapstruct.*;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookEditDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.entity.Book;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookMapper {
    BookDto bookToBookDto(final Book book);

    List<BookDto> booksToBooksDto(final List<Book> book);

    @Mappings({
            @Mapping(target = "authorId", source = "author.id"),
            @Mapping(target = "genresIds", source = "genres", qualifiedByName = "genresToIds")
    })
    BookEditDto bookDtoToBookEditDto(final BookDto model);

    @Named("genresToIds")
    default Set<Long> genresToIds(final List<GenreDto> models) {
        return models.stream().map(GenreDto::getId).collect(Collectors.toSet());
    }
}
