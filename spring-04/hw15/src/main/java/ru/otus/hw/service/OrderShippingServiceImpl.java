package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.model.OrderProduct;
import ru.otus.hw.model.ProcessingProduct;
import ru.otus.hw.warehouse.inventory.Product;
import ru.otus.hw.warehouse.status.Status;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderShippingServiceImpl implements OrderShippingService {

    public OrderProduct stepOrderShipping(final ProcessingProduct product) {
        log.info("Сборщик: {}, начал отправку заказов", product.name());
        final Set<String> products = getProducts(product.products());
        final OrderProduct orderProduct = new OrderProduct(
                product.name(),
                products,
                Status.SHIPPED);
        log.info("Отправленные заказы: {}", orderProduct);
        log.info("Сборщик: {}, отправил продукты: {}", orderProduct.name(), products);
        return orderProduct;
    }

    private Set<String> getProducts(final Set<Product> products) {
        return products.stream()
                .filter(Objects::nonNull)
                .map(Product::getName)
                .collect(Collectors.toSet());
    }
}
