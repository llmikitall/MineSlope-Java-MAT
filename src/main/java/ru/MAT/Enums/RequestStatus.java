package ru.MAT.Enums;

import lombok.Getter;

public enum RequestStatus {
    CREATING("Создание запроса"),
    AWAIT("Ожидание действия"),
    VIEWING("Запрос на рассмотрение"),
    ACCEPT("Запрос принят"),
    DENY("Запрос отклонён");

    @Getter
    private final String description;

    RequestStatus(String description){
        this.description = description;
    }
}
