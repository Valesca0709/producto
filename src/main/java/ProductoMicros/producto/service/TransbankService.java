package main.java.ProductoMicros.producto.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class TransbankService {

    @Value("${transbank.commerce-code}")
    private String commerceCode;

    @Value("${transbank.api-key}")
    private String apiKey;

    @Value("${transbank.api-url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> iniciarTransaccion(int amount, String buyOrder, String sessionId, String returnUrl) {
        String url = apiUrl + "/transactions";

        Map<String, Object> body = new HashMap<>();
        body.put("amount", amount);
        body.put("buy_order", buyOrder);
        body.put("session_id", sessionId);
        body.put("return_url", returnUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Tbk-Api-Key-Id", commerceCode);
        headers.set("Tbk-Api-Key-Secret", apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("error", "No autorizado o error de conexión con Transbank.");
            return error;
        }
    }

    public Map<String, Object> confirmarTransaccion(String token) {
        String url = apiUrl + "/transactions/" + token;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Tbk-Api-Key-Id", commerceCode);
        headers.set("Tbk-Api-Key-Secret", apiKey);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.PUT, request, Map.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al confirmar la transacción.");
            return error;
        }
    }
}