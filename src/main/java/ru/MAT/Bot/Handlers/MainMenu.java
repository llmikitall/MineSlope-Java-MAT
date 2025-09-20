package ru.MAT.Bot.Handlers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
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
public class MainMenu implements BotHandler{
    private final TelegramBot bot;
    private final PlayerRepository playerRepository;
    private final RequestRepository requestRepository;

    public MainMenu(TelegramBot bot, PlayerRepository playerRepository, RequestRepository requestRepository) {
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
        boolean status = player.getStatus().equals(UserStatus.MAIN_MENU);

        List<String> buttons = Arrays.asList("[Отправить жалобу]");
        String text = Optional.ofNullable(update.message().text()).orElse("");
        boolean button = buttons.stream().anyMatch(text::contains);

        return status && button;
    }

    @Override
    public void Handle(Update update){
        boolean fact = Optional.ofNullable(update.message().text())
                .orElse("")
                .contains("[Отправить жалобу]");

        if (fact){

            Long chatId = update.message().chat().id();
            Player player = playerRepository.findByTgId(chatId);

            player.setRequestType(RequestType.CLAIM_ABOUT_PLAYER);
            player.setStatus(UserStatus.CLAIM_TO_PLAYER_MENU);

            playerRepository.save(player);

            List<Request> requests = requestRepository.findByTgIdAndRequestType(chatId, RequestType.CLAIM_ABOUT_PLAYER);

            SendMessage message = ClaimToPlayerMenu.OutputClaimToPlayerMenu(chatId, requests);
            bot.execute(message);
        }
    }

    public static SendMessage OutputMainMenu(Long chatId){

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup("\uD83D\uDEA8 [Отправить жалобу]");
        keyboard.resizeKeyboard(true);

        String output = "<b>[Главное меню]</b>:";

        return new SendMessage(chatId, output)
                .replyMarkup(keyboard)
                .parseMode(ParseMode.HTML);
    }

    public static SendMessage MessageMainMenu(Long chatId){

        String output = "<blockquote>\uD83D\uDC4B Привет, Слоуповец!\n" +
                "————————————————\n" +
                "\uD83E\uDD16 Я СлопеБотик — твой личный помощник в мире MineSlope!\n" +
                "\n" +
                "┌───────────────────\n" +
                "⚙\uFE0F Нахожусь в ОБТ и имею\n" +
                "│ не так много функций.   \n" +
                "│Однако всё ещё впереди!   \n" +
                "└───────────────────\n" +
                "┌───────────────────\n" +
                "│ \uD83D\uDDA5 IP: mc.mineslope.ru\n" +
                "│ \uD83C\uDF10 Сайт: mineslope.ru\n" +
                "│ \uD83D\uDCAC Чат: @mineslopetg\n" +
                "│ \uD83D\uDCE2 ТГК: @mineslope\n" +
                "│ \uD83D\uDC68\u200D\uD83D\uDCBB Dev: iambread\n" +
                "└───────────────────</blockquote>";

        return new SendMessage(chatId, output).parseMode(ParseMode.HTML);
    }

}
