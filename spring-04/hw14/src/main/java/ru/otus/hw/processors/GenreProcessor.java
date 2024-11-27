package ru.otus.hw.processors;

import org.springframework.batch.item.ItemProcessor;
import ru.otus.hw.dto.mongo.GenreDto;
import ru.otus.hw.model.Genre;

public class GenreProcessor implements ItemProcessor<Genre, GenreDto> {

    @Override
    public GenreDto process(final Genre item) {
        return new GenreDto(item.getId(), item.getName());
    }
}
