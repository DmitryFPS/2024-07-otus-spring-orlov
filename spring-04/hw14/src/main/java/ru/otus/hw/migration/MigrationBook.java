package ru.otus.hw.migration;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.dto.mongo.BookDto;
import ru.otus.hw.model.Book;
import ru.otus.hw.processors.BookProcessor;
import ru.otus.hw.repositories.mongo.MongoBookRepository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;

import static ru.otus.hw.common.MigrationConstants.CHUNK_SIZE;

/**
 * Миграция для Книг
 */
@RequiredArgsConstructor
@Component
public class MigrationBook {
    private final DataSource dataSource;

    private final JobRepository jobRepository;

    private final MongoBookRepository mongoBookRepository;

    private final PlatformTransactionManager platformTransactionManager;


    /**
     * Создает временную таблицу temp_table_book_mongo_to_h2 для хранения идентификаторов книг из базы данных mongo и их
     * соответствующих идентификаторов в базе данных h2
     *
     * @return Экземпляр шага TaskletStep
     */
    @Bean
    public TaskletStep createTemporaryBook() {
        return new StepBuilder("createTemporaryBook", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                    new JdbcTemplate(dataSource).execute(
                            "CREATE TABLE IF NOT EXISTS temp_table_book_mongo_to_h2 " +
                                    "(id_mongo VARCHAR(255) NOT NULL UNIQUE, id_h2 BIGINT NOT NULL UNIQUE)"
                    );
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                .build();
    }

    /**
     * Создает Reader, который считывает все объекты Book из репозитория mongoBookRepository
     *
     * @return Экземпляр Reader RepositoryItemReader
     */
    @Bean
    public RepositoryItemReader<Book> bookReader() {
        return new RepositoryItemReaderBuilder<Book>()
                .name("bookReader")
                .repository(mongoBookRepository)
                .methodName("findAll")
                .pageSize(10)
                .sorts(new HashMap<>())
                .build();
    }

    /**
     * Создает Writer, который вставляет данные из объектов BookDto во временную таблицу temp_table_book_mongo_to_h2
     *
     * @return Экземпляр Writer JdbcBatchItemWriter
     */
    @Bean
    public JdbcBatchItemWriter<BookDto> bookInsertTempTable() {
        final JdbcBatchItemWriter<BookDto> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("INSERT INTO temp_table_book_mongo_to_h2(id_mongo, id_h2) VALUES (:id, nextval('seq_book_h2'))");
        return writer;
    }

    /**
     * Создает экземпляр JdbcBatchItemWriter для вставки данных из объектов BookDto в таблицу books
     * Этот компонент Spring Batch настраивается следующим образом:
     * setItemPreparedStatementSetter: лямбда-выражение, которое заполняет подготовленный
     * оператор значениями из объекта BookDto
     * setSql: SQL-запрос для вставки данных в таблицу books
     * setDataSource: источник данных для подключения к базе данных
     *
     * @return экземпляр JdbcBatchItemWriter
     */
    @Bean
    public JdbcBatchItemWriter<BookDto> bookJdbcBatchItemWriter() {
        final JdbcBatchItemWriter<BookDto> writer = new JdbcBatchItemWriter<>();
        writer.setItemPreparedStatementSetter((bookDto, statement) -> {
            statement.setString(1, bookDto.getTitle());
            statement.setString(2, bookDto.getId());
            statement.setString(3, bookDto.getAuthorId());
            statement.setString(4, bookDto.getGenreId());
        });
        writer.setSql("INSERT INTO books(title, id, author_id, genre_id) " +
                "VALUES (?, " +
                "(SELECT id_h2 FROM temp_table_book_mongo_to_h2 WHERE id_mongo = ?), " +
                "(SELECT id_h2 FROM temp_table_author_mongo_to_h2 WHERE id_mongo = ?), " +
                "(SELECT id_h2 FROM temp_table_genre_mongo_to_h2 WHERE id_mongo = ?)" +
                ")");
        writer.setDataSource(dataSource);
        return writer;
    }

    /**
     * Создает экземпляр CompositeItemWriter для объединения нескольких экземпляров JdbcBatchItemWriter
     * Этот компонент Spring Batch настраивается следующим образом:
     * setDelegates: список экземпляров JdbcBatchItemWriter, которые будут объединены
     * При использовании этого компонента данные будут обрабатываться всеми объединенными
     * экземплярами JdbcBatchItemWriter по порядку
     *
     * @param bookInsertTempTable     экземпляр JdbcBatchItemWriter для вставки данных во временную таблицу
     * @param bookJdbcBatchItemWriter экземпляр JdbcBatchItemWriter для вставки данных в таблицу books
     * @return экземпляр CompositeItemWriter
     */
    @Bean
    public CompositeItemWriter<BookDto> compositeBookWriter(
            final JdbcBatchItemWriter<BookDto> bookInsertTempTable,
            final JdbcBatchItemWriter<BookDto> bookJdbcBatchItemWriter) {

        final CompositeItemWriter<BookDto> writer = new CompositeItemWriter<>();
        writer.setDelegates(List.of(bookInsertTempTable, bookJdbcBatchItemWriter));
        return writer;
    }

    /**
     * Создает шаг bookMigrationStep, который читает книги из базы данных mongo, преобразует их в объекты BookDto,
     * вставляет их во временную таблицу, а затем вставляет их в таблицу книг в базе данных h2
     *
     * @param reader    reader, который считывает книги из базы данных mongo
     * @param writer    Составной writer, который вставляет данные во временную таблицу и затем в таблицу книг
     * @param processor Процессор, который преобразует объекты Book в объекты BookDto
     * @return Экземпляр шага Step
     */
    @Bean
    public Step bookMigrationStep(final RepositoryItemReader<Book> reader,
                                  final CompositeItemWriter<BookDto> writer,
                                  final BookProcessor processor) {
        return new StepBuilder("bookMigrationStep", jobRepository)
                .<Book, BookDto>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .allowStartIfComplete(true)
                .build();
    }

    /**
     * Создает последовательность seq_book_h2 для базы данных h2 в генерации уникальных идентификаторов для книг
     *
     * @return Экземпляр шага TaskletStep
     */
    @Bean
    public TaskletStep createSeqH2Book() {
        return new StepBuilder("createSeqH2Book", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                            new JdbcTemplate(dataSource).execute("CREATE SEQUENCE IF NOT EXISTS seq_book_h2");
                            return RepeatStatus.FINISHED;
                        }),
                        platformTransactionManager)
                .build();
    }

    /**
     * Удаляет временную таблицу temp_table_book_mongo_to_h2 после завершения миграции
     *
     * @return Экземпляр шага TaskletStep
     */
    @Bean
    public TaskletStep dropTemporaryBook() {
        return new StepBuilder("dropTemporaryBook", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                            new JdbcTemplate(dataSource)
                                    .execute("DROP TABLE temp_table_book_mongo_to_h2");
                            return RepeatStatus.FINISHED;
                        }),
                        platformTransactionManager)
                .build();
    }

    /**
     * Удаляет последовательность seq_book_h2 после завершения миграции
     *
     * @return Экземпляр шага TaskletStep
     */
    @Bean
    public TaskletStep dropSeqH2Book() {
        return new StepBuilder("dropSeqH2Book", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                            new JdbcTemplate(dataSource)
                                    .execute("DROP SEQUENCE IF EXISTS seq_book_h2");
                            return RepeatStatus.FINISHED;
                        }),
                        platformTransactionManager)
                .build();
    }

    /**
     * Создает процессор, который преобразует объекты Book из базы данных mongo в объекты BookDto,
     * которые будут вставлены в базу данных h2
     *
     * @return Экземпляр процессора BookProcessor
     */
    @Bean
    public BookProcessor bookProcessorProcessor() {
        return new BookProcessor();
    }
}
