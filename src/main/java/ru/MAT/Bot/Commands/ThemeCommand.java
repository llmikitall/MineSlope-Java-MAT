package ru.MAT.Bot.Commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;

@Component
public class ThemeCommand implements BotCommand{

    private final TelegramBot bot;

    public ThemeCommand(TelegramBot bot) {
        this.bot = bot;
    }

    @Override
    public boolean CanHandle(Update update){
        return update.message().text().equals("!Тема");
    }

    @Override
    public void Handle(Update update){
        String output = String.format("<b>[ID чата]</b>: %d\n<b>[ID темы]</b>: %d", update.message().chat().id(),
                update.message().messageThreadId());
        SendMessage sendMessage = new SendMessage(update.message().from().id(), output).parseMode(ParseMode.HTML);
        bot.execute(sendMessage);

        DeleteMessage deleteMessage = new DeleteMessage(update.message().chat().id(), update.message().messageId());
        bot.execute(deleteMessage);
    }
}
