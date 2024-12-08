package ru.otus.hw.model;

import ru.otus.hw.warehouse.status.Status;

import java.util.Set;

public record OrderProduct(String name, Set<String> products, Status status) {
}
