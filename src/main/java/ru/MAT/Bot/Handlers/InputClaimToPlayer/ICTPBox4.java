package ru.MAT.Bot.Handlers.InputClaimToPlayer;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.MAT.Bot.FileDownloadService;
import ru.MAT.Bot.Handlers.BotHandler;
import ru.MAT.Entities.Player;
import ru.MAT.Entities.Request;
import ru.MAT.Enums.UserStatus;
import ru.MAT.Repository.PlayerRepository;
import ru.MAT.Repository.RequestRepository;

import java.io.IOException;
import java.util.*;

@Component
public class ICTPBox4 implements BotHandler {

    private final PlayerRepository playerRepository;
    private final RequestRepository requestRepository;
    private final TelegramBot bot;
    private final FileDownloadService fileDownloadService;
    private final Map<String, List<PhotoSize>> mediaGroups = new HashMap<>();

    public ICTPBox4(PlayerRepository playerRepository, RequestRepository requestRepository, TelegramBot bot, FileDownloadService fileDownloadService) {
        this.playerRepository = playerRepository;
        this.requestRepository = requestRepository;
        this.bot = bot;
        this.fileDownloadService = fileDownloadService;
    }

    @Value("${files.storage.path:./files}")
    private String filesDirectory;

    @Override
    public boolean CanHandle(Update update){
        Long chatId = update.message().chat().id();
        Player player = playerRepository.findByTgId(chatId);
        if (player == null){
            return false;
        }
        boolean status = player.getStatus().equals(UserStatus.ICTP_BOX4);

        return status;
    }

    @Override
    public void Handle(Update update) {
        if (update.message().photo() != null){

            HandleMediaGroup(update.message(), update.message().mediaGroupId());
            System.out.println("Учимся-учимся");
        }
        else if(update.message().photo() != null){
            Long chatId = update.message().chat().id();
            Player player = playerRepository.findByTgId(chatId);

            player.setStatus(UserStatus.INPUT_CLAIM_TO_PLAYER_MENU);
            playerRepository.save(player);

            Request request = requestRepository.findByRequestIdAndRequestType(player.getRequestId(), player.getRequestType());

            //request.setBox4("++");
            //requestRepository.save(request);

            List<SendMessage> messages = InputClaimToPlayerMenu.OutputInputClaimToPlayerMenu(update,request);

            bot.execute(messages.get(0));
            bot.execute(messages.get(1));

            System.out.println("Пока ничего...");
        }
        else if (update.message().text().contains("[Назад]")){
            ButtonBack(update);
        }



    }

    public void HandleMediaGroup(Message message, String mediaGroupId){

        PhotoSize[] photo = message.photo();
        PhotoSize largestPhoto = photo[photo.length - 1];

        String fileName = String.format("photo_%s.jpg", message.date());
        String filePath = fileDownloadService.downloadFile(largestPhoto.fileId(), fileName);
        if(filePath == null){
            System.out.println("Ошибка!");
            return;
        }
        System.out.println("Фото сохранено");

        SendMessage response = new SendMessage(message.chat().id(), "Фото сохранено!");
        bot.execute(response);


    }

    public void ButtonBack(Update update){
        Long chatId = update.message().chat().id();
        Player player = playerRepository.findByTgId(chatId);

        player.setStatus(UserStatus.INPUT_CLAIM_TO_PLAYER_MENU);

        Request request = requestRepository.findByRequestIdAndRequestType(player.getRequestId(), player.getRequestType());
        List<SendMessage> messages = InputClaimToPlayerMenu.OutputInputClaimToPlayerMenu(update,request);

        playerRepository.save(player);

        bot.execute(messages.get(0));
        bot.execute(messages.get(1));
    }

    public static SendMessage OutputICTPBox4(Long chatId){
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup("◀ [Назад]");
        keyboard.resizeKeyboard(true);
        return new SendMessage(chatId, "<b>[Отправьте фотографии]</b>")
                .parseMode(ParseMode.HTML)
                .replyMarkup(keyboard);
    }

}
