package ru.otus.hw.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.model.Book;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookMapper {
    BookDto bookToBookDto(final Book book);

    Book bookDtoToBook(final BookDto bookDto);

    List<BookDto> booksToBooksDto(final List<Book> book);
}
