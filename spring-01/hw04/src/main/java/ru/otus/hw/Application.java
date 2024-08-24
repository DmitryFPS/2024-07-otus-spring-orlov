package ru.otus.hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.otus.hw.config.AppProperties;

@EnableConfigurationProperties(AppProperties.class)
@SpringBootApplication
public class Application {
    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
