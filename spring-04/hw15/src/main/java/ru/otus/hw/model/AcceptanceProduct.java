package ru.otus.hw.model;

import ru.otus.hw.warehouse.status.Status;

import java.util.Set;
import java.util.UUID;

public record AcceptanceProduct(String name, Set<UUID> articles, Status status) {
}
