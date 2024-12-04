package ru.otus.hw.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.model.AcceptanceProduct;
import ru.otus.hw.model.OrderProduct;
import ru.otus.hw.warehouse.inventory.Product;
import ru.otus.hw.warehouse.status.Status;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DeliveryGatewayTest {
    @Autowired
    private DeliveryGateway deliveryGateway;

    @Test
    void testProcess() {
        final Set<UUID> article = Set.of(
                Product.APPLE.getArticle(), Product.COFFEE.getArticle(), Product.ORANGE.getArticle());
        final List<AcceptanceProduct> acceptanceProducts = List.of(
                new AcceptanceProduct("Dima", article, Status.ADOPTED));

        final List<OrderProduct> actual = deliveryGateway.process(acceptanceProducts);

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).name()).isEqualTo("Dima");
        assertThat(actual.get(0).products()).containsExactlyInAnyOrder(
                Product.APPLE.getName(), Product.COFFEE.getName(), Product.ORANGE.getName());
        assertThat(actual.get(0).status()).isEqualTo(Status.SHIPPED);
    }
}
