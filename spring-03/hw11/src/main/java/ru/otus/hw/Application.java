package ru.otus.hw;

import com.github.cloudyrock.spring.v5.EnableMongock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.sql.SQLException;

@EnableMongock
@EnableMongoRepositories
@SpringBootApplication
public class Application {
    public static void main(String[] args) throws SQLException {
        SpringApplication.run(Application.class, args);

        System.out.println();
        System.out.printf("Чтобы перейти на страницу сайта через 'npm run start' открывай: %n%s%n",
                "http://localhost:8080");
        System.out.printf("Чтобы перейти на страницу сайта через 'npm run dev' открывай: %n%s%n",
                "http://localhost:9000");
        System.out.println();

    }
}
