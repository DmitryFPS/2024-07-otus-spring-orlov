package ru.otus.hw.model;

import ru.otus.hw.warehouse.inventory.Product;
import ru.otus.hw.warehouse.status.Status;

import java.util.Set;

public record ProcessingProduct(String name, Set<Product> products, Status status) {
}
