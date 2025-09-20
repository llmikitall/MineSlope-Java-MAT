package ru.MAT.Bot.Handlers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendSticker;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
public class BadMessage{
    private final TelegramBot bot;

    public BadMessage(TelegramBot bot) {
        this.bot = bot;
    }

    public boolean CanHandle(Update update){
        return true;
    }

    public void Handle(Update update){
        Long chatId = update.message().chat().id();
        String output = "Не пойму, что ты вводишь...";

        SendMessage message = new SendMessage(chatId, output);
        bot.execute(message);

        SendSticker sticker = new SendSticker(chatId, "CAACAgIAAxkBAAEJq5FnJQaTjxkeDffS6Ye1ygynAAEb2tsAAiAJAAKm2dBISzXq3TBjArA2BA");
        bot.execute(sticker);
    }

}
