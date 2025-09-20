package ru.MAT.Enums;

import lombok.Getter;

public enum RequestType {
    CLAIM_ABOUT_PLAYER("Жалоба на игрока");

    @Getter
    private final String description;

    RequestType(String description){
        this.description = description;
    }
}
