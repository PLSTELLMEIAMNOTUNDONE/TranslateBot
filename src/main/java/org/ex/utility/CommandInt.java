package org.ex.utility;

import org.ex.constants.Command;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

public interface CommandInt {
    public Command getCommand();

    public BotApiMethod<?> execute(String text, Long chatId);

    public String getDesc();
}
