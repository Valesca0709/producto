package ProductoMicros.producto.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class SseController {
    // Lista de todos los clientes conectados
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final AtomicInteger sequence = new AtomicInteger(0);

    @GetMapping("/sse/stream")
    public SseEmitter stream() {
        SseEmitter emitter = new SseEmitter(0L); // Sin timeout
        emitters.add(emitter);

        // 1. Conexión establecida correctamente
        try {
            emitter.send(SseEmitter.event().name("connected").data("Conexión SSE establecida"));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        // 3. Reconexión automática y 8. Cerrar conexión desde servidor
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));

        // 2. Recepción de mensajes, 4. Orden de mensajes, 5. Manejo de datos JSON
        executor.submit(() -> {
            try {
                while (true) {
                    Thread.sleep(1000);
                    int seq = sequence.incrementAndGet();
                    // 4. Orden de mensajes
                    emitter.send(SseEmitter.event().name("mensaje").data("Mensaje número: " + seq));
                    // 5. Manejo de datos JSON
                    Map<String, Object> jsonPayload = Map.of("id", seq, "mensaje", "Mensaje JSON " + seq);
                    emitter.send(SseEmitter.event().name("json").data(jsonPayload, MediaType.APPLICATION_JSON));
                }
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        // 7. Prueba de rendimiento (enviar 100 eventos rápidos)
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

    // 9. Manejo de múltiples conexiones: broadcast a todos los clientes
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

    // Endpoint para cerrar la conexión de todos los clientes manualmente
    @GetMapping("/sse/close")
    public String closeAll() {
        for (SseEmitter emitter : emitters) {
            emitter.complete();
        }
        emitters.clear();
        return "Conexiones SSE cerradas.";
    }

    // Endpoint para forzar la desconexión SSE manualmente desde el frontend
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
