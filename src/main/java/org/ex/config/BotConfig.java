package org.ex.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@PropertySource("classpath:application.properties")
public class BotConfig {
    @Value("${telegrambot.botUsername}")
    String botUsername;
    @Value("${telegrambot.botPath}")
    String botPath;
    @Value("${telegrambot.botId}")
    String botId;

}
