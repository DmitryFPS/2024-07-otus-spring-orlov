package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class GenreController {

    private final GenreService genreService;

    @GetMapping("/genre")
    public String findGenres(final Model model) {
        final List<GenreDto> genres = genreService.findAll();
        model.addAttribute("genres", genres);
        return "genrePages/genres";
    }

    @GetMapping("/genre/{id}")
    public String findGenreById(@PathVariable("id") final long id,
                                final Model model) {
        final GenreDto genre = genreService.findById(id);
        model.addAttribute("genre", genre);
        return "genrePages/genre";
    }
}
