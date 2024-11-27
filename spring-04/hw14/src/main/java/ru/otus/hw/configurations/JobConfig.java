package ru.otus.hw.configurations;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.hw.migration.MigrationAuthor;
import ru.otus.hw.migration.MigrationBook;
import ru.otus.hw.migration.MigrationComment;
import ru.otus.hw.migration.MigrationGenre;

import static ru.otus.hw.common.JobConstants.MIGRATE_JOB_NAME;


/**
 * Конфигурация джобы
 */
@RequiredArgsConstructor
@Configuration
public class JobConfig {
    private final JobRepository jobRepository;

    private final MigrationAuthor migrationAuthor;

    private final MigrationGenre migrationGenre;

    private final MigrationComment migrationComment;

    private final MigrationBook migrationBook;


    /**
     * Настроить задачу, которая мигрирует данные из базы данных {@code mongo} в базу данных {@code h2}
     *
     * @param authorMigrationStep  Шаг, который мигрирует авторов из БД {@code mongo} в БД {@code h2}
     * @param genreMigrationStep   Шаг, который мигрирует жанры из БД {@code mongo} в БД {@code h2}
     * @param commentMigrationStep Шаг, который мигрирует комментарии из БД {@code mongo} в БД {@code h2}
     * @param bookMigrationStep    Шаг, который мигрирует книги из БД {@code mongo} в БД {@code h2}
     * @return Экземпляр {@code Job}, который выполняет миграцию
     */
    @Bean
    public Job migrateJob(final Step authorMigrationStep,
                          final Step genreMigrationStep,
                          final Step commentMigrationStep,
                          final Step bookMigrationStep) {
        return new JobBuilder(MIGRATE_JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer()) //настраивает генератор идентификаторов заданий
                .start(migrationAuthor.createTemporaryAuthor()) //создание временной таблицы temp_table_author_mongo
                .next(migrationGenre.createTemporaryGenre()) //создание временной таблицы temp_table_genre_mongo
                .next(migrationBook.createTemporaryBook()) //создание временной таблицы temp_table_book_mongo
                .next(migrationComment.createTemporaryComment()) //создание временной таблицы temp_table_comment_mongo
                .next(migrationAuthor.createSeqH2Author()) // создает последовательность в Author для id в h2
                .next(migrationGenre.createSeqH2Genre()) // создает последовательность в Genre для id в h2
                .next(migrationBook.createSeqH2Book()) // создает последовательность в Book для id в h2
                .next(migrationComment.createSeqH2Comment()) // создает последовательность в Comment для id в h2
                .next(authorMigrationStep).next(genreMigrationStep).next(bookMigrationStep).next(commentMigrationStep)
                .next(migrationAuthor.dropTemporaryAuthor()) // удаление временной таблицы temp_table_author_mongo
                .next(migrationGenre.dropTemporaryGenre()) // удаление временной таблицы temp_table_genre_mongo
                .next(migrationBook.dropTemporaryBook()) // удаление временной таблицы temp_table_book_mongo
                .next(migrationComment.dropTemporaryComment()) // удаление временной таблицы temp_table_comment_mongo
                .next(migrationAuthor.dropSeqH2Author()) // удаление последовательности в Author для id в h2
                .next(migrationGenre.dropSeqH2Genre()) // удаление последовательности в Genre для id в h2
                .next(migrationBook.dropSeqH2Book()) // удаление последовательности в Book для id в h2
                .next(migrationComment.dropSeqH2Comment()) // удаление последовательности в Comment для id в h2
                .build();
    }
}
