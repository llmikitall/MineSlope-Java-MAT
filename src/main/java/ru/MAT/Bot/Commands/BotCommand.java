package ru.MAT.Bot.Commands;

import com.pengrad.telegrambot.model.Update;

public interface BotCommand {
    boolean CanHandle(Update update);
    void Handle(Update update);
}
