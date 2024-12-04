package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.gateway.DeliveryGateway;
import ru.otus.hw.model.AcceptanceProduct;
import ru.otus.hw.warehouse.inventory.Product;
import ru.otus.hw.warehouse.status.Status;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import java.util.random.RandomGenerator;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductAcceptanceServiceImpl implements ProductAcceptanceService {
    private static final List<String> COLLECTOR_NAMES = List.of("John", "Jane", "Peter", "Mary", "David");

    private static final int MAXIMUM_NUMBER_ATTEMPTS = 100;

    private static final int NUMBER_ATTEMPTS = 3;

    private static final int ACCEPTANCES = 7;

    private final DeliveryGateway deliveryGateway;


    public void startAcceptanceProduct() {
        ForkJoinPool.commonPool().execute(() -> {
            final List<AcceptanceProduct> products = generateAcceptanceProducts();
            deliveryGateway.process(products);
        });
    }

    private List<AcceptanceProduct> generateAcceptanceProducts() {
        final List<AcceptanceProduct> acceptanceProducts = new ArrayList<>(ACCEPTANCES);
        for (int i = 0; i < ACCEPTANCES; i++) {
            acceptanceProducts.add(generateAcceptanceProduct());
        }

        return acceptanceProducts;
    }

    private AcceptanceProduct generateAcceptanceProduct() {
        final int indexName = RandomGenerator.getDefault().nextInt(COLLECTOR_NAMES.size());
        final Set<UUID> uuids = new HashSet<>();

        int attempt = 0;
        while (attempt < MAXIMUM_NUMBER_ATTEMPTS) {
            uuids.add(Product.ARTICLES.get(RandomGenerator.getDefault().nextInt(Product.ARTICLES.size())));
            attempt++;
            if (uuids.size() >= NUMBER_ATTEMPTS) {
                break;
            }
        }

        if (uuids.size() < NUMBER_ATTEMPTS) {
            throw new RuntimeException(("Ошибка во время получения заказа, не удалось обработать нужное " +
                    "количество артикулов - %s").formatted(NUMBER_ATTEMPTS));
        }

        final AcceptanceProduct acceptanceProduct =
                new AcceptanceProduct(COLLECTOR_NAMES.get(indexName), uuids, Status.ADOPTED);

        log.info("Сборщик: {}, принял заказ в обработку", acceptanceProduct.name());
        log.info("Заказ принят в обработку: {}", acceptanceProduct);
        return acceptanceProduct;
    }
}
