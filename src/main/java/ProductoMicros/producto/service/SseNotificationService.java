package ProductoMicros.producto.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.ObjectMapper;

import ProductoMicros.producto.model.ProductoSucursal;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseNotificationService {
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public void addEmitter(SseEmitter emitter) {
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));
    }
    
    public void notificarStockBajo(ProductoSucursal productoSucursal) {
        Map<String, Object> alerta = new HashMap<>();
        alerta.put("tipo", "STOCK_BAJO_COMPRA");
        alerta.put("producto", productoSucursal.getProducto().getNombre());
        alerta.put("sucursal", productoSucursal.getSucursal().getNombre());
        alerta.put("stock", productoSucursal.getStock());
        alerta.put("mensaje", "🛒 Después de una compra: " + productoSucursal.getProducto().getNombre() + " en " + productoSucursal.getSucursal().getNombre() + " ahora tiene " + productoSucursal.getStock() + " unidades");
        
        try {
            String jsonString = objectMapper.writeValueAsString(alerta);
            
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event().name("stock-bajo-compra").data(jsonString, MediaType.APPLICATION_JSON));
                } catch (IOException e) {
                    emitter.completeWithError(e);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al notificar stock bajo después de compra: " + e.getMessage());
        }
    }
    
    public List<SseEmitter> getEmitters() {
        return emitters;
    }
    
    public void clearEmitters() {
        emitters.clear();
    }
}
