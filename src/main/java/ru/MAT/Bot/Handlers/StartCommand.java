package ru.MAT.Bot.Handlers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import ru.MAT.Entities.Player;
import ru.MAT.Entities.Request;
import ru.MAT.Enums.RequestStatus;
import ru.MAT.Enums.UserStatus;
import ru.MAT.Repository.PlayerRepository;
import ru.MAT.Repository.RequestRepository;

@Component
public class StartCommand implements BotHandler {
    private final TelegramBot bot;
    private final PlayerRepository playerRepository;
    private final RequestRepository requestRepository;

    public StartCommand(TelegramBot bot, PlayerRepository playerRepository, RequestRepository requestRepository) {
        this.bot = bot;
        this.playerRepository = playerRepository;
        this.requestRepository = requestRepository;
    }


    @Override
    public boolean CanHandle(Update update){
        return update.message().text().equals("/start");
    }

    @Override
    public void Handle(Update update){
        Long chatId = update.message().chat().id();

        Player player = playerRepository.findByTgId(chatId);

        if(player == null){
            System.out.printf("[>] Новый пользователь! [%d]\n", chatId);
            player = new Player();
            player.setTgId(chatId);
        }

        if(player.isMessageMainMenu()){
            player.setMessageMainMenu(false);
            SendMessage message = MainMenu.MessageMainMenu(chatId);
            bot.execute(message);
        }

        if(player.getRequestId() != 0){
            Request request = requestRepository.findByRequestIdAndRequestType(player.getRequestId(), player.getRequestType());
            if (request.getRequestStatus() == RequestStatus.CREATING){
                requestRepository.delete(request);
            }
        }

        player.setRequestType(null);
        player.setRequestId(0);
        player.setStatus(UserStatus.MAIN_MENU);


        playerRepository.save(player);

        SendMessage message = MainMenu.OutputMainMenu(chatId);

        bot.execute(message);
    }
}
