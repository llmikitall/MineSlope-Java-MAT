package ru.MAT.Bot.Handlers;

import com.pengrad.telegrambot.model.Update;
import ru.MAT.Entities.Player;
import ru.MAT.Enums.UserStatus;

public interface BotPhotoHandler {
    boolean CanPhotoHandle(UserStatus status);
    void Handle(Player player, Update update);
}
