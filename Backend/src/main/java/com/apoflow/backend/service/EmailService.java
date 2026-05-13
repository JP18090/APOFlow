package com.apoflow.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private static final String MAILERSEND_URL = "https://api.mailersend.com/v1/email";

    @Value("${mailersend.token:}")
    private String apiToken;

    @Value("${mailersend.from:MS_apoflow@trial-3z0vklo6omeldpyo.mlsender.net}")
    private String fromAddress;

    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void send(String to, String subject, String body) {
        if (!emailEnabled) {
            System.out.printf("[EMAIL] Para: %s | Assunto: %s%n%s%n----%n", to, subject, body);
            return;
        }
        try {
            Map<String, Object> payload = Map.of(
                    "from", Map.of("email", fromAddress, "name", "APOFlow"),
                    "to", List.of(Map.of("email", to)),
                    "subject", subject,
                    "text", body
            );

            String json = objectMapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(MAILERSEND_URL))
                    .header("Content-Type", "application/json")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .header("Authorization", "Bearer " + apiToken)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                System.out.printf("[EMAIL] Enviado para %s (status %d)%n", to, response.statusCode());
            } else {
                System.err.printf("[EMAIL] Falha ao enviar para %s: HTTP %d — %s%n", to, response.statusCode(), response.body());
            }
        } catch (Exception e) {
            System.err.println("[EMAIL] Erro ao enviar para " + to + ": " + e.getMessage());
        }
    }

    public void sendOtp(String to, String nome, String otp) {
        String subject = "APOFlow – Seu código de verificação";
        String body = String.format(
                "Olá, %s!%n%nSeu código de verificação para acesso ao APOFlow é:%n%n    %s%n%n" +
                "Ele é válido por 10 minutos.%nSe você não solicitou este código, ignore este e-mail.%n%n" +
                "— Equipe APOFlow / PPG-CA Mackenzie",
                nome, otp);
        send(to, subject, body);
    }

    public void sendApoNotification(String to, String nome, String titulo, String evento) {
        String subject = "APOFlow – Atualização na sua APO";
        String body = String.format(
                "Olá, %s!%n%nHouve uma atualização na APO \"%s\":%n%n    %s%n%n" +
                "Acesse o APOFlow para mais detalhes.%n%n— Equipe APOFlow / PPG-CA Mackenzie",
                nome, titulo, evento);
        send(to, subject, body);
    }
}
