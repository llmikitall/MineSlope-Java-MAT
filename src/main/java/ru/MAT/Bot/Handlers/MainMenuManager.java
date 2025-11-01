package ru.MAT.Bot.Handlers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import ru.MAT.Entities.Player;
import ru.MAT.Enums.UserStatus;
import ru.MAT.Repository.PlayerRepository;

@Component
public class MainMenuManager {
    private final TelegramBot bot;
    private final PlayerRepository playerRepository;

    public MainMenuManager(TelegramBot bot, PlayerRepository playerRepository) {
        this.bot = bot;
        this.playerRepository = playerRepository;
    }

    public void showMainMenu(Player player){
        if(player.isMessageMainMenu()){
            sendWelcomeMessage(player);
        }

        player.setStatus(UserStatus.MAIN_MENU);

        playerRepository.save(player);

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup("\uD83D\uDEA8 [Отправить жалобу]");
        keyboard.resizeKeyboard(true);

        String output = "<b>[Главное меню]</b>:";

        bot.execute(new SendMessage(player.getTgId(), output)
                .replyMarkup(keyboard)
                .parseMode(ParseMode.HTML));


    }

    private void sendWelcomeMessage(Player player){
        String output = "<blockquote>\uD83D\uDC4B Привет, Слоуповец!\n" +
                "————————————————\n" +
                "\uD83E\uDD16 Я СлопеБотик — твой личный помощник в мире MineSlope!\n\n" +
                "┌───────────────────\n" +
                "⚙️ Нахожусь в ОБТ и имею\n" +
                "│ не так много функций.   \n" +
                "│Однако всё ещё впереди!   \n" +
                "└───────────────────\n" +
                "┌───────────────────\n" +
                "│ \uD83D\uDDA5 IP: mc.mineslope.ru\n" +
                "│ \uD83C\uDF10 Сайт: mineslope.ru\n" +
                "│ \uD83D\uDCAC Чат: @mineslopetg\n" +
                "│ \uD83D\uDCE2 ТГК: @mineslope\n" +
                "│ \uD83D\uDC68\u200D\uD83D\uDCBB Dev: \uD83C\uDF5E\n" +
                "└───────────────────</blockquote>";

        player.setMessageMainMenu(false);
        bot.execute(new SendMessage(player.getTgId(), output).parseMode(ParseMode.HTML));
    }
}
