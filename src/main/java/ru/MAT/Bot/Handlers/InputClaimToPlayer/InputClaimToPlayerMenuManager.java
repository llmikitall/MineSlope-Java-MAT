package ru.MAT.Bot.Handlers.InputClaimToPlayer;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.MessagesResponse;
import com.pengrad.telegrambot.response.SendResponse;
import org.springframework.stereotype.Component;
import ru.MAT.Bot.Handlers.RequestProcessing;
import ru.MAT.Entities.Player;
import ru.MAT.Entities.Request;
import ru.MAT.Entities.RequestDraft;
import ru.MAT.Enums.RequestStatus;
import ru.MAT.Enums.UserStatus;
import ru.MAT.Repository.PlayerRepository;
import ru.MAT.Repository.RequestDraftRepository;
import ru.MAT.Repository.RequestRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class InputClaimToPlayerMenuManager {
    private final PlayerRepository playerRepository;
    private final RequestRepository requestRepository;
    private final RequestProcessing requestProcessing;
    private final RequestDraftRepository requestDraftRepository;
    private final TelegramBot bot;

    public InputClaimToPlayerMenuManager(PlayerRepository playerRepository, RequestRepository requestRepository, RequestProcessing requestProcessing, RequestDraftRepository requestDraftRepository, TelegramBot bot) {
        this.playerRepository = playerRepository;
        this.requestRepository = requestRepository;
        this.requestProcessing = requestProcessing;
        this.requestDraftRepository = requestDraftRepository;
        this.bot = bot;
    }

    public void showInputClaimToPlayerMenu(Player player, Update update){
        player.setStatus(UserStatus.INPUT_CLAIM_TO_PLAYER_MENU);

        RequestDraft request = player.getCurrentRequest();

        // Создание кнопок

        ReplyKeyboardMarkup keyboard;
        if (request.isEditable()){
            keyboard = new ReplyKeyboardMarkup(
                    String.format("\uD83D\uDC64 [Мой ник]: %s", request.getBox0().equals("-") ? "✖" : "✔"),
                    String.format("\uD83D\uDCA2 [Его ник]: %s", request.getBox1().equals("-") ? "✖" : "✔"));
            keyboard.addRow(
                    String.format("\uD83D\uDCCC [Тип]: %s", request.getBox2().equals("-") ? "✖" : "✔"),
                    String.format("\uD83C\uDF10 [Коорд.]: %s", request.getBox3().equals("-") ? "✖" : "✔"));
            keyboard.addRow(
                    String.format("\uD83D\uDCF7 [Фото]: %s", request.getBox4().size() == 0 ? "✖" : "✔"),
                    String.format("\uD83D\uDCCB [Детали]: %s", request.getBox5().equals("-") ? "✖" : "✔"));
            keyboard.addRow("◀ [Назад]", "[Сохранить] ▶");
        }
        else{
            keyboard = new ReplyKeyboardMarkup("◀ [Назад]");
        }

        if(request.getRequestStatus() == RequestStatus.AWAIT || request.getRequestStatus() == RequestStatus.VIEWING)
            keyboard.addRow("\uD83D\uDEAB [Отозвать жалобу]");


        if (request.getBox4().size() == 0)
            bot.execute(new SendMessage(player.getTgId(), mainTextMessage(request)).parseMode(ParseMode.HTML));
        else{
            requestProcessing.sendMediaGroup(mainTextMessage(request), request.getBox4(), player.getTgId(), null);
        }

        playerRepository.save(player);

        bot.execute(new SendMessage(player.getTgId(), statusTextMessage(request, update))
                .parseMode(ParseMode.HTML)
                .replyMarkup(keyboard));
    }

    public void buttonBack(Player player){
        RequestDraft requestDraft = player.getCurrentRequest();
        if(requestDraft.getRequestStatus().equals(RequestStatus.CREATING)){
            Request request = requestRepository.findByRequestTypeAndRequestId(requestDraft.getRequestType(), requestDraft.getRequestId());
            requestRepository.delete(request);
        }

        player.setCurrentRequest(null);
        playerRepository.save(player);

        requestDraftRepository.delete(requestDraft);
    }

    public boolean buttonSave(Player player, Update update){

        if((checkEmptyFields(player) || !requestProcessing.checkEditable(player))){
            return false;
        }

        RequestDraft requestDraft = player.getCurrentRequest();

        if(requestDraft.getRequestStatus().equals(RequestStatus.CREATING)){
            requestDraft.setRequestStatus(RequestStatus.AWAIT);

            String mainOutput = mainTextMessage(requestDraft);
            requestDraft.setHtmlMainText(mainOutput);

            if (requestDraft.getBox4().size() == 0){
                SendResponse response = requestProcessing.sendMessage(mainOutput, -1002691896200L, 4);
                requestDraft.setMediaGroupId(List.of(response.message().messageId()));
            }
            else{
                MessagesResponse response = requestProcessing.sendMediaGroup(mainOutput, requestDraft.getBox4(), -1002691896200L, 4);
                List<Integer> mediaGroupIds = new ArrayList<>();
                for(Message message : response.messages()){
                    mediaGroupIds.add(message.messageId());
                }
                requestDraft.setMediaGroupId(mediaGroupIds);
            }
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
            keyboard.addRow(new InlineKeyboardButton("⚙ Действия").callbackData("claim_to_player_actions"));

            String statusOutput = statusTextMessage(requestDraft, update);
            requestDraft.setHtmlStatusText(statusOutput);

            SendResponse response = requestProcessing.sendMessage(statusOutput, -1002691896200L, 4, keyboard);

            requestDraft.setTelegramMessageId(response.message().messageId());

            bot.execute(new SendMessage(player.getTgId(), "<b>Жалоба успешно составлена!</b>\nОстаётся только подождать...").parseMode(ParseMode.HTML));
        }
        else{
            // Редактирование сообщения
        }
        Request request = requestRepository.findByRequestTypeAndRequestId(requestDraft.getRequestType(), requestDraft.getRequestId());

        requestRepository.save(requestProcessing.convertRequestDraft(request, requestDraft));

        player.setCurrentRequest(null);
        playerRepository.save(player);

        requestDraftRepository.delete(requestDraft);

        return true;
    }

    private boolean checkEmptyFields(Player player){
        RequestDraft request = player.getCurrentRequest();

        StringBuilder checkFollowingBox = new StringBuilder("<b>[Введите хотя бы основную информацию]</b>:\n");
        if(request.getBox0().equals("-"))
            checkFollowingBox.append("- Ваш ник;\n");
        if(request.getBox1().equals("-"))
            checkFollowingBox.append("- Ник нарушителя;\n");
        if(request.getBox2().equals("-"))
            checkFollowingBox.append("- Тип нарушения\n");

        if(checkFollowingBox.toString().contains("-")){
            bot.execute(new SendMessage(player.getTgId(), checkFollowingBox.toString()).parseMode(ParseMode.HTML));
            return true;
        }

        return false;
    }

    private String mainTextMessage(RequestDraft request){
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
                builder.append(String.format("  <b>%d) Координаты:</b> %s\n", ++index, request.getBox3()));
            if (!Objects.equals(request.getBox5(), "-"))
                builder.append(String.format("  <b>%d) Детали:</b> %s\n", ++index, request.getBox5()));
            if (request.getBox4().size() != 0)
                builder.append(String.format("  <b>%d) Фотофиксация:</b> ✅\n", ++index));
        }
        else
            builder = new StringBuilder(request.getHtmlMainText());

        return builder.toString();
    }

    private String statusTextMessage(RequestDraft request, Update update){
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
            builder.append("<a href='https://t.me/MineSlopeBot'>✍️</a>");
            builder.append(" <b>Написано</b>: ");
            builder.append(userLink);
            builder.append(separator);
        }
        else
            builder = new StringBuilder(request.getHtmlStatusText());

        return builder.toString();
    }

}
