package ru.otus.hw.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.entity.Genre;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface GenreMapper {
    GenreDto genreToGenreDto(final Genre genre);

    List<GenreDto> genresToGenresDto(final List<Genre> genre);
}
