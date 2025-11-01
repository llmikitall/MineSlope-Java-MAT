package ru.MAT.Bot.Callbacks;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.message.MaybeInaccessibleMessage;
import org.springframework.stereotype.Component;

@Component
public interface BotCallbackHandler {
    boolean CanHandle(String data);
    void Handle(Update update);
}
