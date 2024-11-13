package ru.otus.hw.mongock.events;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import ru.otus.hw.model.Book;
import ru.otus.hw.repositories.CommentRepository;

@Component
@RequiredArgsConstructor
public class MongoBookCascadeDeleteEventListener extends AbstractMongoEventListener<Book> {
    private final CommentRepository commentRepository;

    @Override
    public void onAfterDelete(@NonNull final AfterDeleteEvent<Book> event) {
        super.onAfterDelete(event);

        final Document document = event.getSource();
        final String id = String.valueOf(document.get("_id"));

        commentRepository.findByBookId(id)
                .flatMap(comment -> commentRepository.deleteAll(Flux.just(comment)))
                .subscribe();
    }
}
