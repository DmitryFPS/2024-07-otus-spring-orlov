package ru.otus.hw.processors;

import org.springframework.batch.item.ItemProcessor;
import ru.otus.hw.dto.mongo.AuthorDto;
import ru.otus.hw.model.Author;

public class AuthorProcessor implements ItemProcessor<Author, AuthorDto> {

    @Override
    public AuthorDto process(final Author item) {
        return new AuthorDto(item.getId(), item.getFullName());
    }
}
