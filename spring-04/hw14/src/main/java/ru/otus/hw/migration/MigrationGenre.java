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
import ru.otus.hw.dto.mongo.GenreDto;
import ru.otus.hw.model.Genre;
import ru.otus.hw.processors.GenreProcessor;
import ru.otus.hw.repositories.mongo.MongoGenreRepository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;

import static ru.otus.hw.common.MigrationConstants.CHUNK_SIZE;


/**
 * Миграция для Жанров
 */
@RequiredArgsConstructor
@Component
public class MigrationGenre {
    private final DataSource dataSource;

    private final MongoGenreRepository mongoGenreRepository;

    private final JobRepository jobRepository;

    private final PlatformTransactionManager platformTransactionManager;


    /**
     * Создает временную таблицу temp_table_genre_mongo_to_h2 для хранения идентификаторов
     * жанров из базы данных mongo и их соответствующих идентификаторов в базе данных h2
     *
     * @return Экземпляр шага TaskletStep
     */
    @Bean
    public TaskletStep createTemporaryGenre() {
        return new StepBuilder("createTemporaryGenre", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                            new JdbcTemplate(dataSource).execute(
                                    "CREATE TABLE temp_table_genre_mongo_to_h2 " +
                                            "(id_mongo VARCHAR(255) NOT NULL UNIQUE, id_h2 bigint NOT NULL UNIQUE)"
                            );
                            return RepeatStatus.FINISHED;
                        }),
                        platformTransactionManager)
                .build();
    }

    /**
     * Создает последовательность seq_genre_h2 для базы данных h2 в генерации уникальных идентификаторов для жанров
     *
     * @return Экземпляр шага TaskletStep
     */
    @Bean
    public TaskletStep createSeqH2Genre() {
        return new StepBuilder("createSeqH2Genre", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                            new JdbcTemplate(dataSource).execute("CREATE SEQUENCE IF NOT EXISTS seq_genre_h2");
                            return RepeatStatus.FINISHED;
                        }),
                        platformTransactionManager)
                .build();
    }

    /**
     * Создает Reader, который считывает все объекты Genre из репозитория mongoGenreRepository
     *
     * @return Экземпляр Reader RepositoryItemReader
     */
    @Bean
    public RepositoryItemReader<Genre> genreReader() {
        return new RepositoryItemReaderBuilder<Genre>()
                .name("genreReader")
                .repository(mongoGenreRepository)
                .methodName("findAll")
                .pageSize(10)
                .sorts(new HashMap<>())
                .build();
    }

    /**
     * Создает Writer, который вставляет данные из объектов GenreDto во временную таблицу temp_table_genre_mongo_to_h2
     *
     * @return Экземпляр Writer JdbcBatchItemWriter
     */
    @Bean
    public JdbcBatchItemWriter<GenreDto> genreInsertTempTable() {
        final JdbcBatchItemWriter<GenreDto> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("INSERT INTO temp_table_genre_mongo_to_h2(id_mongo, id_h2) " +
                "VALUES (:id, nextval('seq_genre_h2'))");
        return writer;
    }

    /**
     * Создает Writer, который вставляет данные из объектов GenreDto в таблицу genres в базе данных h2
     *
     * @return Экземпляр Writer JdbcBatchItemWriter
     */
    @Bean
    public JdbcBatchItemWriter<GenreDto> genreJdbcBatchItemWriter() {
        final JdbcBatchItemWriter<GenreDto> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("INSERT INTO genres(id, name) " +
                "VALUES ((SELECT id_h2 FROM temp_table_genre_mongo_to_h2 WHERE id_mongo = :id), :name)");
        return writer;
    }

    /**
     * Объединяет два Writer в один составной Writer. Сначала данные вставляются во временную таблицу,
     * затем данные из временной таблицы вставляются в таблицу жанров
     *
     * @param genreInsertTempTable     Writer, который вставляет данные во временную таблицу
     * @param genreJdbcBatchItemWriter Writer, который вставляет данные из временной таблицы в таблицу жанров
     * @return Экземпляр составного Writer CompositeItemWriter
     */
    @Bean
    public CompositeItemWriter<GenreDto> compositeGenreWriter(
            final JdbcBatchItemWriter<GenreDto> genreInsertTempTable,
            final JdbcBatchItemWriter<GenreDto> genreJdbcBatchItemWriter) {

        final CompositeItemWriter<GenreDto> writer = new CompositeItemWriter<>();
        writer.setDelegates(List.of(genreInsertTempTable, genreJdbcBatchItemWriter));
        return writer;
    }

    /**
     * Создает шаг genreMigrationStep, который читает жанры из базы данных mongo, преобразует их в объекты GenreDto,
     * вставляет их во временную таблицу, а затем вставляет их в таблицу жанров в базе данных h2
     *
     * @param reader    reader, который считывает жанры из базы данных mongo
     * @param writer    Составной writer, который вставляет данные во временную таблицу и затем в таблицу жанров
     * @param processor Процессор, который преобразует объекты Genre в объекты GenreDto
     * @return Экземпляр шага Step
     */
    @Bean
    public Step genreMigrationStep(final RepositoryItemReader<Genre> reader,
                                   final CompositeItemWriter<GenreDto> writer,
                                   final GenreProcessor processor) {
        return new StepBuilder("genreMigrationStep", jobRepository)
                .<Genre, GenreDto>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .allowStartIfComplete(true)
                .build();
    }

    /**
     * Удаляет временную таблицу temp_table_genre_mongo_to_h2 после завершения миграции
     *
     * @return Экземпляр шага TaskletStep
     */
    @Bean
    public TaskletStep dropTemporaryGenre() {
        return new StepBuilder("dropTemporaryGenre", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                            new JdbcTemplate(dataSource).execute(
                                    "DROP TABLE temp_table_genre_mongo_to_h2"
                            );
                            return RepeatStatus.FINISHED;
                        }),
                        platformTransactionManager)
                .build();
    }

    /**
     * Удаляет последовательность seq_genre_h2 после завершения миграции
     *
     * @return Экземпляр шага TaskletStep
     */
    @Bean
    public TaskletStep dropSeqH2Genre() {
        return new StepBuilder("dropSeqH2Genre", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(((contribution, chunkContext) -> {
                            new JdbcTemplate(dataSource)
                                    .execute("DROP SEQUENCE IF EXISTS seq_genre_h2");
                            return RepeatStatus.FINISHED;
                        }),
                        platformTransactionManager)
                .build();
    }

    /**
     * Создает процессор, который преобразует объекты Genre из базы данных mongo в объекты GenreDto,
     * которые будут вставлены в базу данных h2
     *
     * @return Экземпляр процессора GenreProcessor
     */
    @Bean
    public GenreProcessor genreProcessorProcessor() {
        return new GenreProcessor();
    }
}
