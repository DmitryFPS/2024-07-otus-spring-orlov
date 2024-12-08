package ru.otus.hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.otus.hw.service.ProductAcceptanceService;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

        final ProductAcceptanceService productAcceptanceService = context.getBean(ProductAcceptanceService.class);
        productAcceptanceService.startAcceptanceProduct();
    }
}
