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
import ru.otus.hw.dto.mongo.AuthorDto;
import ru.otus.hw.model.Author;
import ru.otus.hw.processors.AuthorProcessor;
import ru.otus.hw.repositories.mongo.MongoAuthorRepository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;

import static ru.otus.hw.common.MigrationConstants.CHUNK_SIZE;


/**
 * Миграция для Авторов
 */
@RequiredArgsConstructor
@Component
public class MigrationAuthor {
    private final DataSource dataSource;

    private final JobRepository jobRepository;

    private final MongoAuthorRepository mongoAuthorRepository;

    private final PlatformTransactionManager platformTransactionManager;


    /**
     * Создает временную таблицу temp_table_author_mongo_to_h2 для хранения идентификаторов
     * авторов из базы данных mongo и их соответствующих идентификаторов в базе данных h2
     *
     * @return Экземпляр шага TaskletStep
     */
    @Bean
    public TaskletStep createTemporaryAuthor() {
        return new StepBuilder("createTemporaryAuthor", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                    new JdbcTemplate(dataSource).execute(
                            "CREATE TABLE IF NOT EXISTS temp_table_author_mongo_to_h2 " +
                                    "(id_mongo VARCHAR(255) NOT NULL UNIQUE, id_h2 BIGINT NOT NULL UNIQUE)"
                    );
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                .build();
    }

    /**
     * Создает последовательность seq_author_h2 для базы данных h2 в генерации уникальных идентификаторов для авторов
     *
     * @return Экземпляр шага TaskletStep
     */
    @Bean
    public TaskletStep createSeqH2Author() {
        return new StepBuilder("createSeqH2Author", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                    new JdbcTemplate(dataSource).execute("CREATE SEQUENCE IF NOT EXISTS seq_author_h2");
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                .build();
    }

    /**
     * Создает Reader, который считывает все объекты Author из репозитория mongoAuthorRepository
     *
     * @return Экземпляр Reader RepositoryItemReader
     */
    @Bean
    public RepositoryItemReader<Author> authorReader() {
        return new RepositoryItemReaderBuilder<Author>()
                .name("authorReader")
                .repository(mongoAuthorRepository)
                .methodName("findAll")
                .pageSize(10)
                .sorts(new HashMap<>())
                .build();
    }

    /**
     * Создает Writer, который вставляет данные из объектов AuthorDto во временную таблицу temp_table_author_mongo_to_h2
     *
     * @return Экземпляр Writer JdbcBatchItemWriter
     */
    @Bean
    public JdbcBatchItemWriter<AuthorDto> authorInsertTempTable() {
        final JdbcBatchItemWriter<AuthorDto> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("INSERT INTO temp_table_author_mongo_to_h2(id_mongo, id_h2) " +
                "VALUES (:id, nextval('seq_author_h2'))");
        return writer;
    }

    /**
     * Создает Writer, который вставляет данные из объектов AuthorDto в таблицу authors в базе данных h2
     *
     * @return Экземпляр Writer JdbcBatchItemWriter
     */
    @Bean
    public JdbcBatchItemWriter<AuthorDto> authorJdbcBatchItemWriter() {
        final JdbcBatchItemWriter<AuthorDto> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("INSERT INTO authors(id, full_name) VALUES " +
                "((SELECT id_h2 FROM temp_table_author_mongo_to_h2 WHERE id_mongo = :id), :fullName)");
        return writer;
    }

    /**
     * Объединяет два Writer в один составной Writer. Сначала данные вставляются во временную таблицу,
     * затем данные из временной таблицы вставляются в таблицу авторов
     *
     * @param authorInsertTempTable     Writer, который вставляет данные во временную таблицу
     * @param authorJdbcBatchItemWriter Writer, который вставляет данные из временной таблицы в таблицу авторов
     * @return Экземпляр составного Writer CompositeItemWriter
     */
    @Bean
    public CompositeItemWriter<AuthorDto> compositeAuthorWriter(
            final JdbcBatchItemWriter<AuthorDto> authorInsertTempTable,
            final JdbcBatchItemWriter<AuthorDto> authorJdbcBatchItemWriter) {

        final CompositeItemWriter<AuthorDto> writer = new CompositeItemWriter<>();
        writer.setDelegates(List.of(authorInsertTempTable, authorJdbcBatchItemWriter));
        return writer;
    }

    /**
     * Создает шаг authorMigrationStep, который читает авторов из базы данных mongo, преобразует их в объекты AuthorDto,
     * вставляет их во временную таблицу, а затем вставляет их в таблицу авторов в базе данных h2
     *
     * @param reader    reader, который считывает авторов из базы данных mongo
     * @param writer    Составной writer, который вставляет данные во временную таблицу и затем в таблицу авторов
     * @param processor Процессор, который преобразует объекты Author в объекты AuthorDto
     * @return Экземпляр шага Step
     */
    @Bean
    public Step authorMigrationStep(final RepositoryItemReader<Author> reader,
                                    final CompositeItemWriter<AuthorDto> writer,
                                    final AuthorProcessor processor) {
        return new StepBuilder("authorMigrationStep", jobRepository)
                .<Author, AuthorDto>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .allowStartIfComplete(true)
                .build();
    }

    /**
     * Удаляет временную таблицу temp_table_author_mongo_to_h2 после завершения миграции
     *
     * @return Экземпляр шага TaskletStep
     */
    @Bean
    public TaskletStep dropTemporaryAuthor() {
        return new StepBuilder("dropTemporaryAuthor", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                            new JdbcTemplate(dataSource)
                                    .execute("DROP TABLE temp_table_author_mongo_to_h2");
                            return RepeatStatus.FINISHED;
                        }),
                        platformTransactionManager)
                .build();
    }

    /**
     * Удаляет последовательность seq_author_h2 после завершения миграции
     *
     * @return Экземпляр шага TaskletStep
     */
    @Bean
    public TaskletStep dropSeqH2Author() {
        return new StepBuilder("dropTemporaryAuthor", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                            new JdbcTemplate(dataSource)
                                    .execute("DROP SEQUENCE IF EXISTS seq_author_h2");
                            return RepeatStatus.FINISHED;
                        }),
                        platformTransactionManager)
                .build();
    }

    /**
     * Создает процессор, который преобразует объекты Author из базы данных mongo в объекты AuthorDto,
     * которые будут вставлены в базу данных h2
     *
     * @return Экземпляр процессора AuthorProcessor
     */
    @Bean
    public AuthorProcessor authorProcessor() {
        return new AuthorProcessor();
    }
}
