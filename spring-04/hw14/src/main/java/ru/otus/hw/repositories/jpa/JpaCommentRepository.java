package ru.otus.hw.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.entity.Comment;

public interface JpaCommentRepository extends JpaRepository<Comment, Long> {
}
