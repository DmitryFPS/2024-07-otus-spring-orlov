package ru.otus.hw.warehouse.status;

import lombok.Getter;

@Getter
public enum Status {
    ADOPTED("Принят"),
    ASSEMBLING("Обрабатывается"),
    SHIPPED("Отправлен"),
    ;


    private final String status;

    Status(final String status) {
        this.status = status;
    }
}
