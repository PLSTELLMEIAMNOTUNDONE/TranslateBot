package org.ex.handlers;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.ex.constants.Command;
import org.ex.models.User;
import org.ex.utility.CommandInt;
import org.ex.utility.FileProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.ex.service.UserService;

import java.util.HashMap;
import java.util.Locale;

@Component
public class MessageHandler {
    @Autowired
    UserService userService;

    @Autowired
    FileProcessor fileProcessor;
    @Getter
    String Info = "Бот позволяет переводит текста прямо внутри файлов, отправте файл и получите его копию с переведенным текстом \n используейте комманды /SwitchRequestFile и /SwitchResponseFile чтобы изменить расширение отправляемого и получемого файлов соотвественно ";
    @Getter
    HashMap<Command, CommandInt> commandSet = new HashMap<>();

    @PostConstruct
    public void fillSet() {
        commandSet.put(
                Command.SWITCHREQUESTFILE, new CommandInt() {
                    @Override
                    public Command getCommand() {
                        return Command.SWITCHREQUESTFILE;
                    }

                    @Override
                    public SendMessage execute(String text, Long chatId) {
                        return switchRequestCommand(text, chatId);
                    }

                    @Override
                    public String getDesc() {
                        return """
                                Изменяет расширение отправляемого  файла,\s
                                Формат использования /SwitchRequestFile + расширение файла\s
                                """;
                    }
                }
        );
        commandSet.put(
                Command.SWITCHRESPONSEFILE, new CommandInt() {
                    @Override
                    public Command getCommand() {
                        return Command.SWITCHRESPONSEFILE;
                    }

                    @Override
                    public SendMessage execute(String text, Long chatId) {
                        return switchResponseCommand(text, chatId);
                    }

                    @Override
                    public String getDesc() {
                        return """
                                Изменяет расширение  получаемого файла,\s
                                Формат использования /SwitchResponseFile + расширение файла\s
                                """;
                    }
                }
        );
        commandSet.put(
                Command.STATUS, new CommandInt() {
                    @Override
                    public Command getCommand() {
                        return Command.STATUS;
                    }

                    @Override
                    public SendMessage execute(String text, Long chatId) {
                        return status(text, chatId);
                    }

                    @Override
                    public String getDesc() {
                        return "Получить текущие настройки\n";
                    }
                }
        );
        commandSet.put(
                Command.INFO, new CommandInt() {
                    @Override
                    public Command getCommand() {
                        return Command.INFO;
                    }

                    @Override
                    public SendMessage execute(String text, Long chatId) {
                        return info(text, chatId);
                    }

                    @Override
                    public String getDesc() {
                        return "Получить информацию о боте\n";
                    }
                }
        );
        commandSet.put(
                Command.SET, new CommandInt() {
                    @Override
                    public Command getCommand() {
                        return Command.SET;
                    }

                    @Override
                    public SendMessage execute(String text, Long chatId) {
                        return set(text, chatId);
                    }

                    @Override
                    public String getDesc() {
                        return "список существующих команд\n";
                    }
                }
        );
        commandSet.put(
                Command.SWITCHREQUESTLANG, new CommandInt() {
                    @Override
                    public Command getCommand() {
                        return Command.SWITCHREQUESTLANG;
                    }

                    @Override
                    public SendMessage execute(String text, Long chatId) {
                        return switchRequestLangCommand(text, chatId);
                    }

                    @Override
                    public String getDesc() {
                        return """
                                сменить язык отправляемого файла,
                                Формат использования /SwitchRequestLang + первые две буквы в названии языка
                                """;
                    }
                }
        );
        commandSet.put(
                Command.SWITCHRESPONSELANG, new CommandInt() {
                    @Override
                    public Command getCommand() {
                        return Command.SWITCHRESPONSELANG;
                    }

                    @Override
                    public SendMessage execute(String text, Long chatId) {
                        return switchResponseLangCommand(text, chatId);
                    }

                    @Override
                    public String getDesc() {
                        return """
                                сменить язык получаемого файла,
                                Формат использования /SwitchResponseLang + первые две буквы в названии языка
                                """;
                    }
                }
        );

    }

    public BotApiMethod<?> handle(Message message) {
        String[] text = message.getText().split(" ");
        for (String line : text) line = line.toLowerCase(Locale.ROOT);
        text[0] = text[0].replaceFirst("/", "");

        for (Command command : Command.values()) {
            if (text[0].equals(command.toString().toLowerCase())) {
                return commandSet.get(command).execute(text.length > 1 ? text[1] : null, message.getChatId());

            }
        }
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText("?");
        return sendMessage;
    }

    public SendMessage switchRequestCommand(String text, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        if (text == null) sendMessage.setText("Укажите расширение (/info)");
        else if (fileProcessor.switchRequestStance(text.replace(".", ""), chatId.toString()))
            sendMessage.setText("Расширение изменено");
        else sendMessage.setText("Такой тип нее найден(");
        sendMessage.setChatId(chatId);
        return sendMessage;
    }

    public SendMessage switchResponseCommand(String text, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        if (text == null) sendMessage.setText("Укажите расширение (/info)");
        else if (fileProcessor.switchResponseStance(text.replace(".", ""), chatId.toString()))
            sendMessage.setText("Расширение изменено");
        else sendMessage.setText("Такой тип нее найден(");
        sendMessage.setChatId(chatId);
        return sendMessage;
    }

    public SendMessage switchResponseLangCommand(String text, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        if (text == null) sendMessage.setText("Укажите язык (/info)");
        else if (fileProcessor.switchResponseLang(text, chatId.toString())) sendMessage.setText("Язык изменен");
        else sendMessage.setText("Такой язык не поддерживается/не существует(");
        sendMessage.setChatId(chatId);
        return sendMessage;
    }

    public SendMessage switchRequestLangCommand(String text, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        if (text == null) sendMessage.setText("Укажите язык (/info)");
        else if (fileProcessor.switchRequestLang(text, chatId.toString())) sendMessage.setText("Язык изменен");
        else sendMessage.setText("Такой язык не поддерживается/не существует(");
        sendMessage.setChatId(chatId);
        return sendMessage;
    }

    public SendMessage info(String text, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(getInfo());
        sendMessage.setChatId(chatId);
        return sendMessage;
    }

    public SendMessage status(String text, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        User user = userService.getUser(chatId.toString());
        sendMessage.setText(
                "Текущие настройки\n" +
                        "Принимает ." + user.getReqFile().toString().toLowerCase() + "\n" +
                        "Переводит с " + user.getLangFrom() + " на " + user.getLangTo() + " и \n" +
                        "Отправляет ." + user.getResFile().toString().toLowerCase()
        );
        sendMessage.setChatId(chatId);
        return sendMessage;
    }

    public SendMessage set(String text, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        StringBuilder answer = new StringBuilder();
        for (Command command : Command.values()) {
            answer.append(
                    "/" + command.toString().toLowerCase() + " - " + commandSet.get(command).getDesc() + "\n"
            );
        }
        answer.append("""

                список поддерживаемых расширений:\s
                txt\s
                pdf""".indent(1));
        sendMessage.setText(answer.toString());
        sendMessage.setChatId(chatId);
        return sendMessage;
    }
}
