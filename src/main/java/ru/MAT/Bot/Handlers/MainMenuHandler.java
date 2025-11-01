package ru.MAT.Bot.Handlers;

import com.pengrad.telegrambot.model.Update;
import org.springframework.stereotype.Component;
import ru.MAT.Entities.Player;
import ru.MAT.Enums.UserStatus;

import java.util.List;
import java.util.Optional;

@Component
public class MainMenuHandler implements BotHandler {

    private final ClaimToPlayerMenuManager claimToPlayerMenuManager;

    public MainMenuHandler(ClaimToPlayerMenuManager claimToPlayerMenuManager) {
        this.claimToPlayerMenuManager = claimToPlayerMenuManager;
    }


    @Override
    public boolean CanHandle(String text, UserStatus status){
        if(!status.equals(UserStatus.MAIN_MENU))
            return false;

        List<String> buttons = List.of("[Отправить жалобу]");
        text = Optional.ofNullable(text).orElse("");

        return buttons.stream().anyMatch(text::contains);
    }

    @Override
    public void Handle(Player player, Update update){
        String button = update.message().text();

        if (button.contains("[Отправить жалобу]")) {
            claimToPlayerMenuManager.showClaimToPlayerMenu(player);
        }
    }

}
