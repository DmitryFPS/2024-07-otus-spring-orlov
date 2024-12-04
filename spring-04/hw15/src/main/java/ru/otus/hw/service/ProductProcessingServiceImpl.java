package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.model.AcceptanceProduct;
import ru.otus.hw.model.ProcessingProduct;
import ru.otus.hw.warehouse.inventory.Product;
import ru.otus.hw.warehouse.status.Status;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductProcessingServiceImpl implements ProductProcessingService {
    public ProcessingProduct stepProcessingProduct(final AcceptanceProduct product) {
        log.info("Начало сборки заказов, сборщиком: {}", product.name());
        final ProcessingProduct processingProduct = new ProcessingProduct(
                product.name(),
                handleOrders(product.articles()),
                Status.ASSEMBLING);
        log.info("Собранные заказы: {}", processingProduct);
        log.info("Сборщик: {}, закончил сборку заказов", processingProduct.name());
        return processingProduct;
    }

    private Set<Product> handleOrders(final Set<UUID> articles) {
        return articles.stream()
                .filter(Objects::nonNull)
                .map(Product::fromArticle)
                .collect(Collectors.toSet());
    }
}
