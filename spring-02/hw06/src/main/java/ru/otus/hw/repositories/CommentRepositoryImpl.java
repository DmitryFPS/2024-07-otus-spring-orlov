package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.entity.Comment;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {
    @PersistenceContext
    private final EntityManager entityManager;


    @Override
    public Optional<Comment> findById(final Long id) {
        return ofNullable(entityManager.find(Comment.class, id));
    }

    @Override
    public List<Comment> findByBookId(final Long bookId) {
        final String sql = "select c from Comment c where c.book.id = :bookId";
        final TypedQuery<Comment> query = entityManager.createQuery(sql, Comment.class);
        query.setParameter("bookId", bookId);
        return query.getResultList();
    }

    @Override
    public Comment save(final Comment comment) {
        if (isNull(comment.getId())) {
            entityManager.persist(comment);
            return comment;
        }
        return entityManager.merge(comment);
    }

    @Override
    public void deleteById(final Long id) {
        final Comment comment = entityManager.find(Comment.class, id);
        entityManager.remove(comment);
    }
}
