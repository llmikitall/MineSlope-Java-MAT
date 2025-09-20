package ru.MAT.Config;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {
    @Value("${BOT_TOKEN}")
    private String token;

    @Bean
    public TelegramBot telegramBot(){
        return new TelegramBot(token);
    }
}
