package ru.otus.hw.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.entity.Book;
import ru.otus.hw.entity.Genre;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookMapper {
    BookDto bookToBookDto(final Book book);

    List<BookDto> booksToBooksDto(final List<Book> book);

    @Mappings({
            @Mapping(target = "authorId", source = "author.id"),
            @Mapping(target = "genreIds", expression = "java(genresToIds(book.getGenres()))")
    })
    BookUpdateDto bookToBookUpdateDto(final Book book);

    default Set<Long> genresToIds(final List<Genre> genres) {
        return genres.stream()
                .map(Genre::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }


    @Mappings({
            @Mapping(target = "authorId", source = "author.id"),
            @Mapping(target = "genreIds", expression = "java(genresDtoToIds(bookDto.getGenres()))")
    })
    BookUpdateDto bookDtoToBookUpdateDto(final BookDto bookDto);

    default Set<Long> genresDtoToIds(final List<GenreDto> genres) {
        return genres.stream()
                .map(GenreDto::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
