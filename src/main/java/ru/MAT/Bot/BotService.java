package ru.MAT.Bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;
import ru.MAT.Bot.Commands.BotCommand;
import ru.MAT.Bot.Handlers.BadMessage;
import ru.MAT.Bot.Handlers.BotHandler;
import ru.MAT.Bot.Handlers.InputClaimToPlayer.ICTPBox4;

import java.util.List;

@Service
public class BotService {
    private final TelegramBot bot;

    private final List<BotHandler> handlers;
    private final List<BotCommand> commands;
    private final ICTPBox4 photo;

    private final BadMessage badMessage;

    public BotService(TelegramBot bot, List<BotHandler> handlers, List<BotCommand> commands, ICTPBox4 photo, BadMessage badMessage) {
        this.bot = bot;
        this.handlers = handlers;
        this.commands = commands;
        this.photo = photo;
        this.badMessage = badMessage;
    }

    @PostConstruct
    public void start() {
        bot.setUpdatesListener(updates -> {
            updates.forEach(this::processUpdate);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
        System.out.println("[>] TelegramBot запущен.");
    }

    public void processUpdate(Update update){
        boolean text = update.message().text() != null;
        boolean privateChat = update.message().chat().type().equals(Chat.Type.Private);

        boolean handled = false;

        if (privateChat && (update.message().photo() != null || update.message().mediaGroupId() != null)){
            if(photo.CanHandle(update)){
                handled = true;
                photo.Handle(update);
            }
        }

        if (privateChat && text){
            System.out.printf("[%d] %s\n",
                    update.message().chat().id(),
                    update.message().text());

            for (BotHandler handler: handlers)
                if(handler.CanHandle(update)){
                    handled = true;
                    handler.Handle(update);
                    break;
                }
        }

        if(text && update.message().text().startsWith("!")){
            System.out.printf("[%d:%d] %s\n",
                    update.message().chat().id(),
                    update.message().from().id(),
                    update.message().text());

            for (BotCommand command: commands){
                if(command.CanHandle(update)){
                    handled = true;
                    command.Handle(update);
                    break;
                }
            }
        }

        if (privateChat && !handled && badMessage.CanHandle(update)){
            badMessage.Handle(update);
        }

    }

    @PreDestroy
    public void stop(){
        bot.removeGetUpdatesListener();
        System.out.println("[>] TelegramBot остановлен.");
    }
}
