package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exception.NotFoundException;
import ru.otus.hw.mapper.AuthorMapper;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    private final AuthorMapper authorMapper;


    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Transactional(readOnly = true)
    @Override
    public List<AuthorDto> findAll() {
        return authorMapper.authorsToAuthorsDto(authorRepository.findAll());
    }

    @Transactional(readOnly = true)
    @Override
    public AuthorDto findById(final Long id) {
        return authorRepository.findById(id)
                .map(authorMapper::authorToAuthorDto)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Не удалось получить автора по Id: %d", id)));
    }
}
