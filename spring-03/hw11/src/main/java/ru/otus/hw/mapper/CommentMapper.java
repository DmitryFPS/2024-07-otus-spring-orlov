package ru.otus.hw.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.model.Comment;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {
    CommentDto commentToCommentDto(final Comment comment);
}
