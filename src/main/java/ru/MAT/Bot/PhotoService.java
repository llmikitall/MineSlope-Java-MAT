package ru.MAT.Bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.response.GetFileResponse;
import com.pengrad.telegrambot.response.SendResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class PhotoService {

    @Value("${file.storage.path}")
    private String storagePath;

    @Value("${TELEGRAM_PHOTO_SERVICE_ID}")
    private Long TELEGRAM_PHOTO_SERVICE_ID;

    @Value("${TELEGRAM_PHOTO_SERVICE_THREAD}")
    private Integer TELEGRAM_PHOTO_SERVICE_THREAD;

    private final TelegramBot bot;

    public PhotoService(TelegramBot bot) {
        this.bot = bot;
    }

    public String downloadPhoto(String fileId){
        GetFile getFile = new GetFile(fileId);
        GetFileResponse getFileResponse = bot.execute(getFile);
        try {
            if(!getFileResponse.isOk()){
                throw new IOException("Не удалось получить информацию о файле: " + getFileResponse.description());
            }
            byte[] fileData = bot.getFileContent(getFileResponse.file());

            Path path = Paths.get(storagePath, fileId);
            Files.createDirectories(path.getParent());
            Files.write(path, fileData);

            return path.toString();
        }
        catch(IOException e){
            System.out.println();
            return null;
        }

    }

    public String extractFileIdFromMessage(Message message){
        if(message.photo().length > 0)
            return message.photo()[message.photo().length - 1].fileId();

        return null;
    }

    public String updateFileId(String path){
        File file = new File(path);
        SendPhoto sendPhoto = new SendPhoto(TELEGRAM_PHOTO_SERVICE_ID, file);
        sendPhoto.messageThreadId(TELEGRAM_PHOTO_SERVICE_THREAD);

        SendResponse sendResponse = bot.execute(sendPhoto);

        if(sendResponse.isOk())
            return sendResponse.message().photo()[0].fileId();
        return null;
    }


}
