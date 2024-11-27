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
import ru.otus.hw.dto.mongo.CommentDto;
import ru.otus.hw.model.Comment;
import ru.otus.hw.processors.CommentProcessor;
import ru.otus.hw.repositories.mongo.MongoCommentRepository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;

import static ru.otus.hw.common.MigrationConstants.CHUNK_SIZE;


/**
 * Миграция для Комментарий
 */
@RequiredArgsConstructor
@Component
public class MigrationComment {
    private final DataSource dataSource;

    private final JobRepository jobRepository;

    private final MongoCommentRepository mongoCommentRepository;

    private final PlatformTransactionManager platformTransactionManager;


    /**
     * Создает временную таблицу temp_table_comment_mongo_to_h2 для хранения идентификаторов комментарий
     * из базы данных mongo и их соответствующих идентификаторов в базе данных h2
     *
     * @return Экземпляр шага TaskletStep
     */
    @Bean
    public TaskletStep createTemporaryComment() {
        return new StepBuilder("createTemporaryComment", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                            new JdbcTemplate(dataSource).execute(
                                    "CREATE TABLE temp_table_comment_mongo_to_h2 " +
                                            "(id_mongo VARCHAR(255) NOT NULL UNIQUE, id_h2 BIGINT NOT NULL UNIQUE)"
                            );
                            return RepeatStatus.FINISHED;
                        }),
                        platformTransactionManager)
                .build();
    }

    /**
     * Создает последовательность seq_comment_h2 для базы данных h2 в генерации
     * уникальных идентификаторов для комментариев
     *
     * @return Экземпляр шага TaskletStep
     */
    @Bean
    public TaskletStep createSeqH2Comment() {
        return new StepBuilder("createSeqH2Comment", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                            new JdbcTemplate(dataSource).execute("CREATE SEQUENCE IF NOT EXISTS seq_comment_h2");
                            return RepeatStatus.FINISHED;
                        }),
                        platformTransactionManager)
                .build();
    }

    /**
     * Создает Reader, который считывает все объекты Comment из репозитория mongoCommentRepository
     *
     * @return Экземпляр Reader RepositoryItemReader
     */
    @Bean
    public RepositoryItemReader<Comment> commentReader() {
        return new RepositoryItemReaderBuilder<Comment>()
                .name("commentReader")
                .repository(mongoCommentRepository)
                .methodName("findAll")
                .pageSize(10)
                .sorts(new HashMap<>())
                .build();
    }

    /**
     * Создает Writer, который вставляет данные из объектов CommentDto
     * во временную таблицу temp_table_comment_mongo_to_h2
     *
     * @return Экземпляр Writer JdbcBatchItemWriter
     */
    @Bean
    public JdbcBatchItemWriter<CommentDto> commentInsertTempTable() {
        final JdbcBatchItemWriter<CommentDto> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("INSERT INTO temp_table_comment_mongo_to_h2(id_mongo, id_h2) " +
                "VALUES (:id, nextval('seq_comment_h2'))");
        return writer;
    }

    /**
     * Создает Writer, который вставляет данные из объектов CommentDto в таблицу comments в базе данных h2
     *
     * @return Экземпляр Writer JdbcBatchItemWriter
     */
    @Bean
    public JdbcBatchItemWriter<CommentDto> commentJdbcBatchItemWriter() {
        final JdbcBatchItemWriter<CommentDto> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setItemPreparedStatementSetter((commentDto, statement) -> {
            statement.setString(1, commentDto.getCommentText());
            statement.setString(2, commentDto.getId());
            statement.setString(3, commentDto.getBookId());
        });
        writer.setSql("INSERT INTO comments(comment_text, id, book_id) " +
                "VALUES (?, " +
                "(SELECT id_h2 FROM temp_table_comment_mongo_to_h2 WHERE id_mongo = ?), " +
                "(SELECT id_h2 FROM temp_table_book_mongo_to_h2 WHERE id_mongo = ?))");
        return writer;
    }

    /**
     * Объединяет два Writer в один составной Writer. Сначала данные вставляются во временную таблицу,
     * затем данные из временной таблицы вставляются в таблицу комментарий
     *
     * @param commentInsertTempTable     Writer, который вставляет данные во временную таблицу
     * @param commentJdbcBatchItemWriter Writer, который вставляет данные из временной таблицы в таблицу комментарий
     * @return Экземпляр составного Writer CompositeItemWriter
     */
    @Bean
    public CompositeItemWriter<CommentDto> compositeCommentWriter(
            final JdbcBatchItemWriter<CommentDto> commentInsertTempTable,
            final JdbcBatchItemWriter<CommentDto> commentJdbcBatchItemWriter) {

        final CompositeItemWriter<CommentDto> writer = new CompositeItemWriter<>();
        writer.setDelegates(List.of(commentInsertTempTable, commentJdbcBatchItemWriter));
        return writer;
    }

    /**
     * Создает шаг commentMigrationStep, который читает комментарии из базы данных mongo, преобразует их в
     * объекты CommentDto, вставляет их во временную таблицу, а затем вставляет их в
     * таблицу комментарии в базе данных h2
     *
     * @param reader    reader, который считывает комментарии из базы данных mongo
     * @param writer    Составной writer, который вставляет данные во временную таблицу и затем в таблицу комментарий
     * @param processor Процессор, который преобразует объекты Comment в объекты CommentDto
     * @return Экземпляр шага Step
     */
    @Bean
    public Step commentMigrationStep(final RepositoryItemReader<Comment> reader,
                                     final CompositeItemWriter<CommentDto> writer,
                                     final CommentProcessor processor) {
        return new StepBuilder("commentMigrationStep", jobRepository)
                .<Comment, CommentDto>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .allowStartIfComplete(true)
                .build();
    }

    /**
     * Удаляет временную таблицу temp_table_comment_mongo_to_h2 после завершения миграции
     *
     * @return Экземпляр шага TaskletStep
     */
    @Bean
    public TaskletStep dropTemporaryComment() {
        return new StepBuilder("dropTemporaryComment", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                            new JdbcTemplate(dataSource).execute(
                                    "DROP TABLE temp_table_comment_mongo_to_h2"
                            );
                            return RepeatStatus.FINISHED;
                        }),
                        platformTransactionManager)
                .build();
    }

    /**
     * Удаляет последовательность seq_comment_h2 после завершения миграции
     *
     * @return Экземпляр шага TaskletStep
     */
    @Bean
    public TaskletStep dropSeqH2Comment() {
        return new StepBuilder("dropSeqH2Comment", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                            new JdbcTemplate(dataSource)
                                    .execute("DROP SEQUENCE IF EXISTS seq_comment_h2");
                            return RepeatStatus.FINISHED;
                        }),
                        platformTransactionManager)
                .build();
    }

    /**
     * Создает процессор, который преобразует объекты Comment из базы данных mongo в объекты CommentDto,
     * которые будут вставлены в базу данных h2
     *
     * @return Экземпляр процессора CommentProcessor
     */
    @Bean
    public CommentProcessor commentProcessorProcessor() {
        return new CommentProcessor();
    }
}
