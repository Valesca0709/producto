package ProductoMicros.producto.service;

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

    // Almacén temporal de datos de la compra por token
    private final Map<String, Map<String, Object>> transaccionesTemporales = new HashMap<>();

    public Map<String, Object> iniciarTransaccion(int amount, String buyOrder, String sessionId, String returnUrl,
        String nombreProducto, int cantidad) {

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
            Map<String, Object> respuesta = response.getBody();

            if (respuesta != null && respuesta.get("token") != null) {
                String token = (String) respuesta.get("token");

                // Guardar datos adicionales asociados al token
                Map<String, Object> datosCompra = new HashMap<>();
                datosCompra.put("nombreProducto", nombreProducto);
                datosCompra.put("cantidad", cantidad);
                datosCompra.put("monto", amount);
                transaccionesTemporales.put(token, datosCompra);
            }

            return respuesta;
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
            Map<String, Object> resultado = response.getBody();

            if (transaccionesTemporales.containsKey(token)) {
                resultado.putAll(transaccionesTemporales.get(token));
                transaccionesTemporales.remove(token);
            }

            return resultado;
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("status", "FAILED");
            return error;
        }
    }
}
