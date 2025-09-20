package ru.MAT.Bot.Handlers;

import com.pengrad.telegrambot.model.Update;

public interface BotHandler {
    boolean CanHandle(Update update);
    void Handle(Update update);
}
