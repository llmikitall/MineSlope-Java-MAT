package ru.MAT.Bot.Handlers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import ru.MAT.Bot.Handlers.InputClaimToPlayer.InputClaimToPlayerMenu;
import ru.MAT.Entities.Player;
import ru.MAT.Entities.Request;
import ru.MAT.Enums.RequestType;
import ru.MAT.Enums.UserStatus;
import ru.MAT.Repository.PlayerRepository;
import ru.MAT.Repository.RequestRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class ClaimToPlayerMenu implements BotHandler {
    private final TelegramBot bot;
    private final PlayerRepository playerRepository;
    private final RequestRepository requestRepository;

    public ClaimToPlayerMenu(TelegramBot bot, PlayerRepository playerRepository, RequestRepository requestRepository) {
        this.bot = bot;
        this.playerRepository = playerRepository;
        this.requestRepository = requestRepository;
    }
    @Override
    public boolean CanHandle(Update update){
        Long chatId = update.message().chat().id();
        Player player = playerRepository.findByTgId(chatId);
        if (player == null){
            return false;
        }
        boolean status = player.getStatus().equals(UserStatus.CLAIM_TO_PLAYER_MENU);

        List<String> buttons = Arrays.asList("[Создать новую жалобу]", "[Назад]", "Жалоба №");
        String text = Optional.ofNullable(update.message().text()).orElse("");
        boolean button = buttons.stream().anyMatch(text::contains);

        return status && button;
    }

    @Override
    public void Handle(Update update) {
        if (update.message().text().contains("[Назад]"))
            ButtonBack(update);
        else if (update.message().text().contains("[Создать новую жалобу]"))
            ButtonNewClaim(update);
        else
            bot.execute(new SendMessage(update.message().chat().id(), "Эй! Вы как это посмотрели??"));
    }

    public void ButtonNewClaim(Update update){
        Long chatId = update.message().chat().id();
        Player player = playerRepository.findByTgId(chatId);

        player.setStatus(UserStatus.INPUT_CLAIM_TO_PLAYER_MENU);



        Request request = new Request();
        request.setTgId(chatId);
        request.setRequestType(player.getRequestType());

        Integer requestId = requestRepository.findMaxRequestId(player.getRequestType()) + 1;

        request.setRequestId(requestId);
        player.setRequestId(requestId);

        requestRepository.save(request);
        playerRepository.save(player);

        List<SendMessage> messages = InputClaimToPlayerMenu.OutputInputClaimToPlayerMenu(update, request);
        bot.execute(messages.get(0));
        bot.execute(messages.get(1));
    }

    public void ButtonBack(Update update){
        Long chatId = update.message().chat().id();
        Player player = playerRepository.findByTgId(chatId);

        if(player.isMessageMainMenu()){
            player.setMessageMainMenu(false);
            SendMessage message = MainMenu.MessageMainMenu(chatId);
            bot.execute(message);
        }

        player.setRequestType(null);
        player.setStatus(UserStatus.MAIN_MENU);

        playerRepository.save(player);

        SendMessage message = MainMenu.OutputMainMenu(chatId);
        bot.execute(message);
    }


    public static SendMessage OutputClaimToPlayerMenu(Long chatId, List<Request> requests){
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup("\uD83D\uDCDD [Создать новую жалобу]");
        keyboard.addRow("◀ [Назад]");
        for(Request request: requests){
            String button;
            switch (request.getRequestStatus()){
                case DENY -> {
                    button = String.format("❌ Жалоба №%03d", request.getRequestId());
                }
                case AWAIT -> {
                    button = String.format("⚙ Жалоба №%03d", request.getRequestId());
                }
                case ACCEPT -> {
                    button = String.format("✅ Жалоба №%03d", request.getRequestId());
                }
                case VIEWING -> {
                    button = String.format("\uD83D\uDD0D Жалоба №%03d", request.getRequestId());
                }
                default -> {
                    button = String.format("(?) Жалоба №%03d", request.getRequestId());
                }
            }
            keyboard.addRow(button);
        }

        keyboard.resizeKeyboard(true);

        String output = "<b>[Выберите жалобу]</b>:";

        return new SendMessage(chatId, output)
                .replyMarkup(keyboard)
                .parseMode(ParseMode.HTML);

    }
}
