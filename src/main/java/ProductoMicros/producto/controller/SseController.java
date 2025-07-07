package ProductoMicros.producto.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.ObjectMapper; // ✅ NUEVO: Import para convertir a JSON

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class SseController {
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final AtomicInteger sequence = new AtomicInteger(0);
    private final ObjectMapper objectMapper = new ObjectMapper(); // ✅ NUEVO: Instancia para usarla

    @GetMapping("/sse/stream")
    public SseEmitter stream() {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.add(emitter);

        try {
            emitter.send(SseEmitter.event().name("connected").data("Conexión SSE establecida"));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
        
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));

        executor.submit(() -> {
            try {
                while (true) {
                    Thread.sleep(1000);
                    int seq = sequence.incrementAndGet();
                    emitter.send(SseEmitter.event().name("mensaje").data("Mensaje número: " + seq));
                    
                    Map<String, Object> jsonPayload = Map.of("id", seq, "mensaje", "Mensaje JSON " + seq);
                    
                    // ✅ CORRECCIÓN: Convertimos el mapa a un String JSON antes de enviarlo.
                    String jsonString = objectMapper.writeValueAsString(jsonPayload);
                    emitter.send(SseEmitter.event().name("json").data(jsonString, MediaType.APPLICATION_JSON));
                }
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        executor.submit(() -> {
            try {
                Thread.sleep(7000);
                for (int i = 0; i < 100; i++) {
                    emitter.send(SseEmitter.event().name("performance").data("Evento performance: " + i));
                }
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    @GetMapping("/sse/broadcast")
    public String broadcast() {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("broadcast").data("Mensaje broadcast a todos los clientes"));
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        }
        return "Broadcast enviado a todos los clientes conectados.";
    }

    @GetMapping("/sse/close")
    public String closeAll() {
        for (SseEmitter emitter : emitters) {
            emitter.complete();
        }
        emitters.clear();
        return "Conexiones SSE cerradas.";
    }

    @GetMapping("/sse/error")
    public String forceError() {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("error").data("Error simulado en SSE (botón)"));
                emitter.completeWithError(new RuntimeException("Error simulado manualmente"));
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        }
        emitters.clear();
        return "Error simulado enviado a todos los clientes SSE.";
    }
}