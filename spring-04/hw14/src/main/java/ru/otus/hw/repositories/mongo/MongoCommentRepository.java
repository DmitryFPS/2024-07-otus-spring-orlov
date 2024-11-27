package ru.otus.hw.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.model.Comment;

public interface MongoCommentRepository extends MongoRepository<Comment, String> {
}
