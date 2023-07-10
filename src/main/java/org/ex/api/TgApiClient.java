package org.ex.api;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.ApiResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.text.MessageFormat;
import java.util.Objects;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)

public class TgApiClient {
    final String url;
    final String token;
    final RestTemplate restTemplate;

    public TgApiClient(@Value("${telegrambot.url}") String url, @Value("${telegrambot.botId}") String token) {
        this.url = url;
        this.token = token;
        this.restTemplate = new RestTemplate();
    }

    public void uploadFile(String chatId, ByteArrayResource value) {
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("document", value);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, httpHeaders);
        try {
            restTemplate.exchange(

                    //url + "bot" + token + "/sendDocument?chat_id=" + chatId,
                    MessageFormat.format("{0}bot{1}/sendDocument?chat_id={2}", url, token, chatId),
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File getDocument(String fileId) {
        try {
            return restTemplate.execute(
                    getDocumentURL(fileId),
                    HttpMethod.GET,
                    null,
                    clientHttpResponse -> {
                        File ret = File.createTempFile("download", "tmp");
                        StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(ret));
                        return ret;
                    }

            );
        } catch (Exception e) {
            throw e;
        }
    }

    public String getDocumentURL(String fileId) {
        try {
            ResponseEntity<ApiResponse<org.telegram.telegrambots.meta.api.objects.File>> response = restTemplate.exchange(
                    MessageFormat.format("{0}bot{1}/getFile?file_id={2}", url, token, fileId),
                    //url + "bot" + token + "/getFile?file_id=" + fileId,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ApiResponse<org.telegram.telegrambots.meta.api.objects.File>>() {
                    }
            );
            return Objects.requireNonNull(response.getBody()).getResult().getFileUrl(this.token);
        } catch (Exception e) {
            throw e;
        }
    }
}
