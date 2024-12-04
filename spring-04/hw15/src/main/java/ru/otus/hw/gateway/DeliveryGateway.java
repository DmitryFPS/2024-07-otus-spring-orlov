package ru.otus.hw.gateway;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.otus.hw.model.AcceptanceProduct;
import ru.otus.hw.model.OrderProduct;

import java.util.List;

@MessagingGateway
public interface DeliveryGateway {
    @Gateway(requestChannel = "acceptanceProductChannel", replyChannel = "orderProductChannel")
    List<OrderProduct> process(final List<AcceptanceProduct> acceptanceProducts);
}
