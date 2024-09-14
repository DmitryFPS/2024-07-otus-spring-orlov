package ru.otus.hw.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.entity.Book;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookMapper {
    BookDto bookToBookDto(final Book book);

    List<BookDto> booksToBooksDto(final List<Book> book);
}
