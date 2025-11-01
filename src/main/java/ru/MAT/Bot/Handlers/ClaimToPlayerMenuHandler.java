package ru.MAT.Bot.Handlers;

import com.pengrad.telegrambot.model.Update;
import org.springframework.stereotype.Component;
import ru.MAT.Bot.Handlers.InputClaimToPlayer.InputClaimToPlayerMenuManager;
import ru.MAT.Entities.Player;
import ru.MAT.Enums.UserStatus;

import java.util.List;
import java.util.Optional;

@Component
public class ClaimToPlayerMenuHandler implements BotHandler {

    private final MainMenuManager mainMenuManager;
    private final ClaimToPlayerMenuManager claimToPlayerMenuManager;
    private final InputClaimToPlayerMenuManager inputClaimToPlayerMenuManager;

    public ClaimToPlayerMenuHandler(MainMenuManager mainMenuManager, ClaimToPlayerMenuManager claimToPlayerMenuManager, InputClaimToPlayerMenuManager inputClaimToPlayerMenuManager) {
        this.mainMenuManager = mainMenuManager;
        this.claimToPlayerMenuManager = claimToPlayerMenuManager;
        this.inputClaimToPlayerMenuManager = inputClaimToPlayerMenuManager;
    }

    @Override
    public boolean CanHandle(String text, UserStatus status) {
        if(!status.equals(UserStatus.CLAIM_TO_PLAYER_MENU))
            return false;

        List<String> buttons = List.of("[Создать новую жалобу]", "[Назад]", "Жалоба №");
        text = Optional.ofNullable(text).orElse("");

        return buttons.stream().anyMatch(text::contains);
    }

    @Override
    public void Handle(Player player, Update update) {
        String button = update.message().text();

        if(button.contains("[Создать новую жалобу]")){
            claimToPlayerMenuManager.createNewClaim(player);
            inputClaimToPlayerMenuManager.showInputClaimToPlayerMenu(player, update);
        } else if (button.contains("[Назад]")) {
            mainMenuManager.showMainMenu(player);
        }
        else{
            // Просмотр жалобы. ОТРЕДАКТИРОВАТЬ
            claimToPlayerMenuManager.viewingClaim(player, 1L);
        }
    }
}
