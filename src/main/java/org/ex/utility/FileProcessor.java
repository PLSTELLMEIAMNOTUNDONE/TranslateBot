package org.ex.utility;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.ex.constants.Lang;
import org.ex.constants.Stance;
import org.ex.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.ex.service.UserService;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@FieldDefaults(level = AccessLevel.PROTECTED)
public class FileProcessor {

    @Autowired
    UserService userService;


    String fontPath = "src/main/data/fonts/ofont.ru_Open Sans.ttf";
    String rootPath = "src/main/data/userDoc/";

    public FileProcessor() {
    }

    public boolean switchResponseStance(String text, String chatId) {
        for (Stance stance : Stance.values()) {
            if (text.equals(stance.toString().toLowerCase())) {
                userService.setUserResFile(chatId, text);
                return true;
            }
        }
        return false;

    }

    public boolean switchRequestStance(String text, String chatId) {
        for (Stance stance : Stance.values()) {
            if (text.equals(stance.toString().toLowerCase())) {
                userService.setUserReqFile(chatId, text);
                return true;
            }
        }
        return false;
    }

    public boolean switchResponseLang(String text, String chatId) {
        for (Lang lang : Lang.values()) {
            if (text.equals(lang.toString().toLowerCase())) {
                userService.setUserLangTo(chatId, text);
                return true;
            }
        }
        return false;
    }

    public boolean switchRequestLang(String text, String chatId) {
        for (Lang lang : Lang.values()) {
            if (text.equals(lang.toString().toLowerCase())) {
                userService.setUserLangFrom(chatId, text);
                return true;
            }
        }
        return false;
    }

    public String getType(File file) {
        String fileName = file.getName();
        StringBuilder type = new StringBuilder();

        for (int i = fileName.length() - 1; i >= fileName.length() - 3; i--) {
            type.append(fileName.charAt(i));
        }
        return type.reverse().toString();
    }

    public File getPdf() {

        PDDocument sample = new PDDocument();
        PDPage page = new PDPage();
        sample.addPage(page);
        PDPageContentStream contentStream = null;
        try {
            contentStream = new PDPageContentStream(sample, page);
            PDFont font = PDType0Font.load(sample, new File(fontPath));

            contentStream.setFont(font, 12);
            contentStream.beginText();
            contentStream.showText("привет");
            contentStream.endText();
            contentStream.close();
            sample.save(rootPath + "test.pdf");
            sample.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new File(rootPath + "test.pdf");

    }

    public String[] pdfText(File file) {
        try {
            PDDocument sourceDocument = PDDocument.load(file);

            PDFTextStripper stripper = new PDFTextStripper();

            String text = stripper.getText(sourceDocument);
            String[] lines = text.split("\\r?\\n");
            for (String line : lines) {

                byte[] b = line.getBytes(StandardCharsets.UTF_8);
                line = new String(b, StandardCharsets.UTF_8);

            }
            return lines;
        } catch (Exception e) {
            return null;
        }

    }

    public File translateFile(File file, String chatId) {
        String[] lines = null;
        User user = userService.getUser(chatId);
        String userReqFile = user.getReqFile();
        String userResFile = user.getResFile();
        if (userReqFile.equals(Stance.PDF.toString().toLowerCase())) {
            lines = pdfText(file);
        }
        if (userReqFile.equals(Stance.TXT.toString().toLowerCase())) {
            lines = txtText(file);
            //lines = new String[]{"жопа"};

        }
        StringBuilder answer = new StringBuilder();

        if (lines != null) {
            for (int i = 0; i < lines.length; i++) {
                try {
                    answer.append(Translator.translate(user.getLangFrom(), user.getLangTo(), lines[i]) + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        } else {

            return null;
        }


        if (userResFile.equals(Stance.PDF.toString().toLowerCase())) try {


            PDDocument sendDocument = new PDDocument();

            PDPage page = new PDPage();
            sendDocument.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(sendDocument, page);
            PDFont font = PDType0Font.load(sendDocument, new File(fontPath));

            contentStream.setFont(font, 12);

            contentStream.beginText();

            contentStream.showText(answer.toString().replace("\n", "").replace("\r", ""));
            contentStream.endText();
            contentStream.close();
            sendDocument.save(rootPath + "translated.pdf");
            sendDocument.close();


            return new File(rootPath + "translated.pdf");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (userResFile.equals(Stance.TXT.toString().toLowerCase())) try {

            File translatedFileTxt = new File(rootPath + "translatedFileTxt.txt");
            PrintWriter printWriter = new PrintWriter(translatedFileTxt);

            printWriter.println(answer.toString());
            printWriter.flush();

            return translatedFileTxt;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return getPdf();
    }

    private String[] txtText(File file) {

        try {
            Scanner scanner = new Scanner(file);


            ArrayList<String> ret = new ArrayList<>();
            while (scanner.hasNextLine()) {
                ret.add(scanner.nextLine());

            }
            scanner.close();
            return ret.toArray(new String[ret.size()]);

        } catch (IOException e) {

            e.printStackTrace();
        }
        return null;
    }
}
