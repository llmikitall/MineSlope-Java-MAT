package ru.MAT.Bot.Callbacks.ClaimToPlayer;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.message.MaybeInaccessibleMessage;
import org.springframework.stereotype.Component;
import ru.MAT.Bot.Callbacks.BotCallbackHandler;

@Component
public class CTPActionsHandler implements BotCallbackHandler {

    private final CTPActionsManager ctpActionsManager;

    public CTPActionsHandler(CTPActionsManager ctpActionsManager) {
        this.ctpActionsManager = ctpActionsManager;
    }

    @Override
    public boolean CanHandle(String data){
        return data.equals("claim_to_player_actions");
    }

    @Override
    public void Handle(Update update){
        ctpActionsManager.showActions(update.callbackQuery().maybeInaccessibleMessage(), update.callbackQuery().id());
    }
}
