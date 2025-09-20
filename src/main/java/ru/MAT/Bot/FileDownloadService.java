package ru.MAT.Bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.response.GetFileResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FileDownloadService {

    @Value("${file.storage.path}")
    private String storagePath;

    private final TelegramBot bot;

    public FileDownloadService(TelegramBot bot) {
        this.bot = bot;
    }

    public String downloadFile(String fileId, String fileName){
        GetFile getFile = new GetFile(fileId);
        GetFileResponse getFileResponse = bot.execute(getFile);
        try {
            if(!getFileResponse.isOk()){
                throw new IOException("Не удалось получить информацию о файле: " + getFileResponse.description());
            }

            byte[] fileData = bot.getFileContent(getFileResponse.file());

            Path path = Paths.get(storagePath, fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, fileData);

            return path.toString();
        }
        catch (IOException e){
            System.out.printf("[!] Error: %s\n", e);
            return null;
        }
    }
}
