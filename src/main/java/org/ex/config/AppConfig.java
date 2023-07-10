package org.ex.config;

import org.ex.bots.TransBot;
import org.ex.handlers.DocumentHandler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;

@Configuration
public class AppConfig {


    private final BotConfig botConfig;

    public AppConfig(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    @Bean
    public SetWebhook setWebhook() {
        return SetWebhook.builder().url(botConfig.getBotPath()).build();
    }

    @Bean
    public TransBot transBot(SetWebhook setWebhook) {
        TransBot transBot = new TransBot(setWebhook);
        transBot.setBotId(botConfig.getBotId());
        transBot.setBotPath(botConfig.getBotPath());
        transBot.setBotUsername(botConfig.getBotUsername());
        return transBot;
    }

}
