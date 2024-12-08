package ru.otus.hw.service;

import ru.otus.hw.model.OrderProduct;
import ru.otus.hw.model.ProcessingProduct;

public interface OrderShippingService {
    OrderProduct stepOrderShipping(final ProcessingProduct processingProduct);
}
