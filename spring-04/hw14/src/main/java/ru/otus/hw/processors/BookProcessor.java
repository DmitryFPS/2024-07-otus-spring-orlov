package ru.otus.hw.processors;

import org.springframework.batch.item.ItemProcessor;
import ru.otus.hw.dto.mongo.BookDto;
import ru.otus.hw.model.Book;

public class BookProcessor implements ItemProcessor<Book, BookDto> {

    @Override
    public BookDto process(final Book item) {
        final String authorId = item.getAuthor().getId();
        final String genreId = item.getGenre().getId();
        return new BookDto(item.getId(), item.getTitle(), authorId, genreId);
    }
}
