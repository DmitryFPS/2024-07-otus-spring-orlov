package ru.otus.hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;

@SpringBootApplication
public class Application {
    public static void main(String[] args) throws SQLException {
        SpringApplication.run(Application.class, args);

        System.out.println();
        System.out.printf("Чтобы перейти на страницу сайта через 'npm run start' открывай: %n%s%n",
                "http://localhost:8080");
        System.out.printf("Чтобы перейти на страницу сайта через 'npm run dev' открывай: %n%s%n",
                "http://localhost:9000");
        System.out.printf("Чтобы перейти в консоль H2: %n%s%n",
                "http://localhost:8080/h2-console");
        System.out.println();

    }
}