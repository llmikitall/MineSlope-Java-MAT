package ru.MAT.Enums;

import lombok.Getter;

public enum UserStatus {
    MAIN_MENU("Главное меню"),
    CLAIM_TO_PLAYER_MENU("Меню отправки жалобы"),
    INPUT_CLAIM_TO_PLAYER_MENU("Меню просмотра/редактирования/составления жалобы"),
    ICTP_BOX0("Жалоба: box0"),
    ICTP_BOX1("Жалоба: box1"),
    ICTP_BOX2("Жалоба: box2"),
    ICTP_BOX3("Жалоба: box3"),
    ICTP_BOX4("Жалоба: box4"),
    ICTP_BOX5("Жалоба: box5");

    @Getter
    private final String description;

    UserStatus(String description){
        this.description = description;
    }
}
