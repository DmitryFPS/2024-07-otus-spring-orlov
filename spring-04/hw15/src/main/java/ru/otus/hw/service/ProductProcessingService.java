package ru.otus.hw.service;

import ru.otus.hw.model.AcceptanceProduct;
import ru.otus.hw.model.ProcessingProduct;

public interface ProductProcessingService {
    ProcessingProduct stepProcessingProduct(final AcceptanceProduct acceptanceProduct);
}
