package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.h2.tools.Console;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;


@RequiredArgsConstructor
@ShellComponent
public class Commands {
    /**
     * джоба по миграции Авторов
     */
    private final Job migrateJob;

    /**
     * средство запуска работы, которое будет использоваться для запуска работы по миграции
     */
    private final JobLauncher jobLauncher;


    /**
     * Запускает миграцию базы данных
     *
     * @throws Exception если миграция не может быть запущена
     */
    @ShellMethod(value = "startMigration", key = "sm-jl")
    public void startMigration() throws Exception {
        final JobExecution jobExecutionByAuthors = jobLauncher.run(migrateJob, new JobParameters());
        System.out.println(jobExecutionByAuthors);
    }

    /**
     * Открывает консоль H2
     */
    @ShellMethod(value = "OpenH2", key = "oh2")
    public String openH2() {
        try {
            Console.main();
            return "Открывается консоль H2";
        } catch (final Exception ex) {
            return "Ошибка при открытии консоли H2: %s".formatted(ex.getLocalizedMessage());
        }
    }
}
