package ru.MAT.Bot.Handlers;

import com.pengrad.telegrambot.model.Update;
import org.springframework.stereotype.Component;
import ru.MAT.Entities.Player;
import ru.MAT.Entities.Request;
import ru.MAT.Entities.RequestDraft;
import ru.MAT.Enums.RequestStatus;
import ru.MAT.Enums.UserStatus;
import ru.MAT.Repository.PlayerRepository;
import ru.MAT.Repository.RequestDraftRepository;
import ru.MAT.Repository.RequestRepository;

@Component
public class StartCommandHandler implements BotHandler {
    private final MainMenuManager mainMenuManager;
    private final PlayerRepository playerRepository;
    private final RequestDraftRepository requestDraftRepository;
    private final RequestRepository requestRepository;

    public StartCommandHandler(MainMenuManager mainMenuManager, PlayerRepository playerRepository, RequestDraftRepository requestDraftRepository, RequestRepository requestRepository) {
        this.mainMenuManager = mainMenuManager;
        this.playerRepository = playerRepository;
        this.requestDraftRepository = requestDraftRepository;
        this.requestRepository = requestRepository;
    }


    @Override
    public boolean CanHandle(String text, UserStatus status){
        return text.equals("/start");
    }

    @Override
    public void Handle(Player player, Update update){

        RequestDraft RequestDraft = player.getCurrentRequest();

        if(RequestDraft != null){
            if(RequestDraft.getRequestStatus().equals(RequestStatus.CREATING)){
                Request request = requestRepository.findByRequestTypeAndRequestId(RequestDraft.getRequestType(), RequestDraft.getRequestId());
                requestRepository.delete(request);
            }
            player.setCurrentRequest(null);
            playerRepository.save(player);

            requestDraftRepository.delete(RequestDraft);
        }

        mainMenuManager.showMainMenu(player);
    }
}
