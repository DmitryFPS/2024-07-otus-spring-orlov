package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.GenreDto;

@Component
public class GenreConverter {
    public String genreToString(final GenreDto genre) {
        return "Id: %d, Name: %s".formatted(genre.getId(), genre.getName());
    }
}
