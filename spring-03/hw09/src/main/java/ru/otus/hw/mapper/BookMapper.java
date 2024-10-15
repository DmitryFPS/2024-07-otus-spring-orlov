package ru.otus.hw.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookEditDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.entity.Book;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookMapper {
    BookDto bookToBookDto(final Book book);

    List<BookDto> booksToBooksDto(final List<Book> book);

    @Mappings({
            @Mapping(target = "author", source = "author.id"),
            @Mapping(target = "genres", expression = "java(genresToIds(bookDto.getGenres()))")
    })
    BookEditDto bookDtoToBookEditDto(final BookDto bookDto);

    default Set<Long> genresToIds(final List<GenreDto> genresDto) {
        return genresDto.stream()
                .map(GenreDto::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
