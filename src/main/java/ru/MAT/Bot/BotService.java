package ru.MAT.Bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.message.MaybeInaccessibleMessage;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;
import ru.MAT.Bot.Callbacks.BotCallbackHandler;
import ru.MAT.Bot.Commands.BotCommand;
import ru.MAT.Bot.Handlers.BadMessage;
import ru.MAT.Bot.Handlers.BotHandler;
import ru.MAT.Bot.Handlers.BotPhotoHandler;
import ru.MAT.Entities.Player;
import ru.MAT.Repository.PlayerRepository;

import java.util.List;

@Service
public class BotService {
    private final TelegramBot bot;

    private final List<BotHandler> handlers;
    private final List<BotCommand> commands;
    private final List<BotPhotoHandler> photos;
    private final List<BotCallbackHandler> callbacks;

    private final PlayerRepository playerRepository;
    private final BadMessage badMessage;

    public BotService(TelegramBot bot, List<BotHandler> handlers, List<BotCommand> commands, List<BotPhotoHandler> photos, List<BotCallbackHandler> callbacks, PlayerRepository playerRepository, BadMessage badMessage) {
        this.bot = bot;
        this.handlers = handlers;
        this.commands = commands;
        this.photos = photos;
        this.callbacks = callbacks;
        this.playerRepository = playerRepository;
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
        if (update.callbackQuery() != null){
            System.out.printf("[%s] %s\n",
                    update.callbackQuery().from().id(),
                    update.callbackQuery().data());

            MaybeInaccessibleMessage message = update.callbackQuery().maybeInaccessibleMessage();
            if(message != null){
                for (BotCallbackHandler callback: callbacks){
                    if(callback.CanHandle(update.callbackQuery().data())){
                        callback.Handle(update);
                        break;
                    }
                }
            }
            else
                bot.execute(new AnswerCallbackQuery(update.callbackQuery().id()));
        }
        else if (update.message().text() != null && update.message().text().startsWith("!")) {
            System.out.printf("[%d:%d] %s\n",
                    update.message().chat().id(),
                    update.message().from().id(),
                    update.message().text());

            // Посмотреть насчёт правильности...
            for (BotCommand command: commands){
                if(command.CanHandle(update)){
                    command.Handle(update);
                    break;
                }
            }
        }
        else if(update.message().chat().type().equals(Chat.Type.Private)){
            Long chatId = update.message().chat().id();
            Player player = playerRepository.findByTgId(chatId);

            if(player == null){
                System.out.printf("[>] Новый пользователь! [%d]\n", chatId);
                player = new Player();
                player.setTgId(chatId);

                playerRepository.save(player);
            }
            boolean handled = true;

            if (update.message().photo() != null){
                System.out.printf("[%d] Отправлено фото\n", update.message().chat().id());

                for (BotPhotoHandler photo: photos)
                    if(photo.CanPhotoHandle(player.getStatus())){
                        photo.Handle(player, update);
                        handled = false;
                        break;
                    }
            }
            else if(update.message().text() != null){
                System.out.printf("[%d] %s\n",
                        update.message().chat().id(),
                        update.message().text());

                for (BotHandler handler: handlers)
                    if(handler.CanHandle(update.message().text(), player.getStatus())){
                        handler.Handle(player, update);
                        handled = false;
                        break;
                    }
            }

            if(handled)
                badMessage.Handle(update);

        }

    }

    @PreDestroy
    public void stop(){
        bot.removeGetUpdatesListener();
        System.out.println("[>] TelegramBot остановлен.");
    }
}
