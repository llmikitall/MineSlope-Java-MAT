package ru.MAT.Bot.Handlers.InputClaimToPlayer;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.springframework.stereotype.Component;
import ru.MAT.Bot.Handlers.BotHandler;
import ru.MAT.Bot.Handlers.ClaimToPlayerMenu;
import ru.MAT.Entities.Player;
import ru.MAT.Entities.Request;
import ru.MAT.Enums.RequestStatus;
import ru.MAT.Enums.UserStatus;
import ru.MAT.Repository.PlayerRepository;
import ru.MAT.Repository.RequestRepository;

import java.util.*;

@Component
public class InputClaimToPlayerMenu implements BotHandler {

    private final TelegramBot bot;
    private final PlayerRepository playerRepository;
    private final RequestRepository requestRepository;

    public InputClaimToPlayerMenu(TelegramBot bot, PlayerRepository playerRepository, RequestRepository requestRepository) {
        this.bot = bot;
        this.playerRepository = playerRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    public boolean CanHandle (Update update){
        Long chatId = update.message().chat().id();
        Player player = playerRepository.findByTgId(chatId);
        if (player == null){
            return false;
        }
        boolean status = player.getStatus().equals(UserStatus.INPUT_CLAIM_TO_PLAYER_MENU);

        List<String> buttons = Arrays.asList("[Назад]", "[Сохранить]", "[Отозвать жалобу]", "[Мой ник]",
                "[Его ник]", "[Тип]", "[Коорд.]", "[Фото]", "[Детали]");
        String text = Optional.ofNullable(update.message().text()).orElse("");
        boolean button = buttons.stream().anyMatch(text::contains);

        return status && button;
    }

    @Override
    public void Handle (Update update){
        if (update.message().text().contains("[Назад]"))
            ButtonBack(update);
        else if (update.message().text().contains("[Сохранить]"))
            ButtonSave(update);
        else if(update.message().text().contains("[Мой ник]"))
            ButtonBox0(update);
        else if(update.message().text().contains("[Его ник]"))
            ButtonBox1(update);
        else if(update.message().text().contains("[Тип]"))
            ButtonBox2(update);
        else if(update.message().text().contains("[Фото]"))
            ButtonBox4(update);
        else
            bot.execute(new SendMessage(update.message().chat().id(), "Ишь какой ты настырный!"));
    }

    public void ButtonBox0(Update update){
        Long chatId = update.message().chat().id();
        Player player = playerRepository.findByTgId(chatId);

        player.setStatus(UserStatus.ICTP_BOX0);

        playerRepository.save(player);

        bot.execute(ICTPBox0.OutputICTPBox0(chatId));
    }

    public void ButtonBox1(Update update){
        Long chatId = update.message().chat().id();
        Player player = playerRepository.findByTgId(chatId);

        player.setStatus(UserStatus.ICTP_BOX1);

        playerRepository.save(player);

        bot.execute(ICTPBox1.OutputICTPBox1(chatId));
    }

    public void ButtonBox2(Update update){
        Long chatId = update.message().chat().id();
        Player player = playerRepository.findByTgId(chatId);

        player.setStatus(UserStatus.ICTP_BOX2);

        playerRepository.save(player);

        bot.execute(ICTPBox2.OutputICTPBox2(chatId));
    }

    public void ButtonBox4(Update update){
        Long chatId = update.message().chat().id();
        Player player = playerRepository.findByTgId(chatId);

        player.setStatus(UserStatus.ICTP_BOX4);

        playerRepository.save(player);

        bot.execute(ICTPBox4.OutputICTPBox4(chatId));
    }

    public void ButtonBack(Update update){
        Long chatId = update.message().chat().id();
        Player player = playerRepository.findByTgId(chatId);

        player.setStatus(UserStatus.CLAIM_TO_PLAYER_MENU);
        Request request = requestRepository.findByRequestIdAndRequestType(player.getRequestId(), player.getRequestType());

        if(request.getRequestStatus() == RequestStatus.CREATING || request.getRequestId() < 0)
            requestRepository.delete(request);

        player.setRequestId(0);

        playerRepository.save(player);

        List<Request> requests = requestRepository.findByTgIdAndRequestType(chatId, player.getRequestType());

        SendMessage message = ClaimToPlayerMenu.OutputClaimToPlayerMenu(chatId, requests);
        bot.execute(message);
    }

    public void ButtonSave(Update update){
        Long chatId = update.message().chat().id();
        Player player = playerRepository.findByTgId(chatId);

        Request request = requestRepository.findByRequestIdAndRequestType(player.getRequestId(), player.getRequestType());

        player.setStatus(UserStatus.CLAIM_TO_PLAYER_MENU);
        player.setRequestId(0);

        if(!request.isEditable()){
            bot.execute(new SendMessage(chatId, "<b>[Данная жалоба либо уже рассматривается, либо была рассмотрена]</b>")
                    .parseMode(ParseMode.HTML));
            playerRepository.save(player);
            return;
        }

        StringBuilder checkFollowingBox = new StringBuilder("<b>[Введите хотя бы основную информацию]</b>:\n");
        if(request.getBox0().equals("-"))
            checkFollowingBox.append("- Ваш ник;\n");
        if(request.getBox1().equals("-"))
            checkFollowingBox.append("- Ник нарушителя;\n");
        if(request.getBox2().equals("-"))
            checkFollowingBox.append("- Тип нарушения\n");

        if(checkFollowingBox.toString().contains("-")){
            bot.execute(new SendMessage(chatId, checkFollowingBox.toString()).parseMode(ParseMode.HTML));
            return;
        }

        if(request.getRequestStatus() == RequestStatus.CREATING){
            request.setRequestStatus(RequestStatus.AWAIT);

            //Отправка медиагруппы и статус сообщения.
            if (request.getBox4().equals("-")){
                SendMessage sendPreviewText = new SendMessage(-1002691896200L, PreviewText(request)).parseMode(ParseMode.HTML);
                sendPreviewText.messageThreadId(4);
                SendResponse response = bot.execute(sendPreviewText);
                request.setMediaGroupId(response.message().messageId().toString());


                InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
                keyboard.addRow(new InlineKeyboardButton("⚙ Действия").callbackData("claim_to_player_actions"));

                SendMessage sendPreviewStatusText = new SendMessage(-1002691896200L, PreviewStatusText(update, request))
                        .parseMode(ParseMode.HTML)
                        .replyMarkup(keyboard);
                sendPreviewStatusText.messageThreadId(4);
                response = bot.execute(sendPreviewStatusText);
                request.setTelegramMessageId(response.message().messageId().toString());

                request.setHtmlText(String.format("%s[StatusText]\n%s", PreviewText(request), PreviewStatusText(update, request)));
            }


            requestRepository.save(request);
            bot.execute(new SendMessage(chatId, "<b>Жалоба успешно составлена!</b>\nОстаётся только подождать...").parseMode(ParseMode.HTML));
        }
        else {
            bot.execute(new SendMessage(chatId, "<b>Прошу простить!</b>\nПока что не получится...").parseMode(ParseMode.HTML));
        }


        playerRepository.save(player);
        List<Request> requests = requestRepository.findByTgIdAndRequestType(chatId, player.getRequestType());

        SendMessage message = ClaimToPlayerMenu.OutputClaimToPlayerMenu(chatId, requests);
        bot.execute(message);


    }

    public static List<SendMessage> OutputInputClaimToPlayerMenu(Update update, Request request){
        Long chatId = update.message().chat().id();
        ReplyKeyboardMarkup keyboard;

        if (request.isEditable()){
            keyboard = new ReplyKeyboardMarkup(
                    String.format("\uD83D\uDC64 [Мой ник]: %s", request.getBox0().equals("-") ? "✖" : "✔"),
                    String.format("\uD83D\uDCA2 [Его ник]: %s", request.getBox1().equals("-") ? "✖" : "✔"));
            keyboard.addRow(
                    String.format("\uD83D\uDCCC [Тип]: %s", request.getBox2().equals("-") ? "✖" : "✔"),
                    String.format("\uD83C\uDF10 [Коорд.]: %s", request.getBox3().equals("-") ? "✖" : "✔"));
            keyboard.addRow(
                    String.format("\uD83D\uDCF7 [Фото]: %s", request.getBox4().equals("-") ? "✖" : "✔"),
                    String.format("\uD83D\uDCCB [Детали]: %s", request.getBox5().equals("-") ? "✖" : "✔"));
            keyboard.addRow("◀ [Назад]", "[Сохранить] ▶");
        }
        else{
            keyboard = new ReplyKeyboardMarkup("◀ [Назад]");
        }

        if(request.getRequestStatus() == RequestStatus.AWAIT || request.getRequestStatus() == RequestStatus.VIEWING)
            keyboard.addRow("\uD83D\uDEAB [Отозвать жалобу]");
        List<SendMessage> sendMessages = new ArrayList<>();
        sendMessages.add(new SendMessage(chatId, PreviewText(request))
                .parseMode(ParseMode.HTML));
        sendMessages.add(new SendMessage(chatId, PreviewStatusText(update,request))
                .parseMode(ParseMode.HTML)
                .replyMarkup(keyboard));
        return sendMessages;
    }

    public static String PreviewText(Request request){
        StringBuilder builder;

        if (request.isEditable()) {
            String separator = "--------------------------------\n";

            // Составление обязательной структуры жалобы
            builder = new StringBuilder(String.format("<b>Жалоба №%03d:</b>\n", Math.abs(request.getRequestId())));
            builder.append(separator);
            builder.append(String.format("  <b>1) Ник игрока:</b> %s\n", request.getBox0()));
            builder.append(String.format("  <b>2) Ник нарушителя:</b> %s\n", request.getBox1()));
            builder.append(String.format("  <b>3) Тип нарушения:</b> %s\n", request.getBox2()));

            // Добавление необязательной структуры жалобы, где index - номер пункта
            int index = 3;
            if (!Objects.equals(request.getBox3(), "-"))
                builder.append(String.format("   <b>%d) Координаты:</b> %s\n", ++index, request.getBox3()));
            if (!Objects.equals(request.getBox4(), "-"))
                builder.append(String.format("   <b>%d) Детали:</b> %s\n", ++index, request.getBox4()));
            if (!Objects.equals(request.getBox5(), "-"))
                builder.append(String.format("   <b>%d) Фотофиксация:</b> ✅\n", ++index));
        }
        else
            builder = new StringBuilder(request.getHtmlText().split("[StatusText]\n")[0]);

        return builder.toString();
    }

    public static String PreviewStatusText(Update update, Request request){
        StringBuilder builder;

        if (request.isEditable()){
            builder = new StringBuilder(String.format("<b>Статус жалобы №%03d</b>: ", Math.abs(request.getRequestId())));

            switch (request.getRequestStatus()){
                case AWAIT -> {
                    builder.append("⚙ Ожидание действий\n");
                }
                case CREATING -> {
                    builder.append("\uD83D\uDCCB Создание жалобы\n");
                }
                default -> {
                    builder.append("Э! Это что за прикол такой?\n");
                    System.out.println("[x] Error: Статус жалобы и редактируемость не совпали.");
                }
            }



            String firstName = update.message().chat().firstName();
            String lastName = update.message().chat().lastName();
            String fullName = String.format("%s%s", firstName, lastName == null ? "" : lastName);

            String userName = update.message().chat().username();
            String userLink = String.format("%s\n", userName == null ? fullName : String.format("<a href='https://t.me/%s'>%s</a>", userName, fullName));
            String separator = "--------------------------------\n";

            builder.append(separator);
            builder.append("<a href='https://t.me/MineSlopeBot'>✍\uFE0F</a>");
            builder.append(" <b>Написано</b>: ");
            builder.append(userLink);
            builder.append(separator);
        }
        else
            builder = new StringBuilder(request.getHtmlText().split("[StatusText]\n")[1]);

        return builder.toString();
    }
}
