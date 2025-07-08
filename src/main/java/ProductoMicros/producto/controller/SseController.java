package ProductoMicros.producto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.ObjectMapper;

import ProductoMicros.producto.model.ProductoSucursal;
import ProductoMicros.producto.service.ProductoSucursalService;
import ProductoMicros.producto.service.SseNotificationService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
public class SseController {
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    private ProductoSucursalService productoSucursalService;
    
    @Autowired
    private SseNotificationService sseNotificationService;

    @GetMapping("/sse/stream")
    public SseEmitter stream() {
        SseEmitter emitter = new SseEmitter(0L);
        sseNotificationService.addEmitter(emitter);

        try {
            emitter.send(SseEmitter.event().name("system").data("Conexión SSE establecida - Monitoreo de stock en tiempo real activo"));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }


        return emitter;
    }

    @GetMapping("/sse/broadcast")
    public String broadcast() {
        List<ProductoSucursal> productosStockBajo = productoSucursalService.obtenerProductosConStockBajo();
        
        if (productosStockBajo.isEmpty()) {
            for (SseEmitter emitter : sseNotificationService.getEmitters()) {
                try {
                    emitter.send(SseEmitter.event().name("system").data("✅ Verificación completada: Todos los productos tienen stock suficiente (≥10 unidades)"));
                    
                    // Enviar evento a la vista principal también
                    Map<String, Object> alertaVistaPrincipal = new HashMap<>();
                    alertaVistaPrincipal.put("producto", "Todos los productos");
                    alertaVistaPrincipal.put("sucursal", "tienen stock suficiente");
                    alertaVistaPrincipal.put("stock", "✅");
                    
                    String jsonVistaPrincipal = objectMapper.writeValueAsString(alertaVistaPrincipal);
                    emitter.send(SseEmitter.event().name("verificacion-manual").data(jsonVistaPrincipal, MediaType.APPLICATION_JSON));
                } catch (IOException e) {
                    emitter.completeWithError(e);
                }
            }
            return "✅ Verificación completada: No hay productos con stock bajo actualmente.";
        }
        
        for (SseEmitter emitter : sseNotificationService.getEmitters()) {
            try {
                for (ProductoSucursal ps : productosStockBajo) {
                    // Evento para el monitor SSE (como estaba antes)
                    Map<String, Object> alerta = new HashMap<>();
                    alerta.put("tipo", "STOCK_BAJO_MANUAL");
                    alerta.put("producto", ps.getProducto().getNombre());
                    alerta.put("sucursal", ps.getSucursal().getNombre());
                    alerta.put("stock", ps.getStock());
                    alerta.put("mensaje", "🔍 VERIFICACIÓN MANUAL: " + ps.getProducto().getNombre() + " en " + ps.getSucursal().getNombre() + " tiene " + ps.getStock() + " unidades (stock bajo)");
                    
                    String jsonString = objectMapper.writeValueAsString(alerta);
                    emitter.send(SseEmitter.event().name("stock-bajo-manual").data(jsonString, MediaType.APPLICATION_JSON));
                    
                    //  Evento para la vista principal (notificaciones toast)
                    Map<String, Object> alertaVistaPrincipal = new HashMap<>();
                    alertaVistaPrincipal.put("producto", ps.getProducto().getNombre());
                    alertaVistaPrincipal.put("sucursal", ps.getSucursal().getNombre());
                    alertaVistaPrincipal.put("stock", ps.getStock());
                    
                    String jsonVistaPrincipal = objectMapper.writeValueAsString(alertaVistaPrincipal);
                    emitter.send(SseEmitter.event().name("verificacion-manual").data(jsonVistaPrincipal, MediaType.APPLICATION_JSON));
                }
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        }
        return "🔍 Verificación manual completada: Se encontraron " + productosStockBajo.size() + " productos con stock bajo.";
    }

    @GetMapping("/sse/close")
    public String closeAll() {
        for (SseEmitter emitter : sseNotificationService.getEmitters()) {
            emitter.complete();
        }
        sseNotificationService.clearEmitters();
        return "Conexiones SSE cerradas.";
    }

    @GetMapping("/sse/error")
    public String forceError() {
        for (SseEmitter emitter : sseNotificationService.getEmitters()) {
            try {
                emitter.send(SseEmitter.event().name("error").data("Error simulado en SSE"));
                emitter.completeWithError(new RuntimeException("Error simulado manualmente"));
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        }
        sseNotificationService.clearEmitters();
        return "Error simulado enviado a todos los clientes SSE.";
    }
    
    // Endpoint para consultar el estado del stock
    @GetMapping("/sse/stock-status")
    public Map<String, Object> getStockStatus() {
        List<ProductoSucursal> productosStockBajo = productoSucursalService.obtenerProductosConStockBajo();
        List<ProductoSucursal> todosLosProductos = productoSucursalService.findAll();
        
        Map<String, Object> status = new HashMap<>();
        status.put("total_productos", todosLosProductos.size());
        status.put("productos_stock_bajo", productosStockBajo.size());
        status.put("productos_stock_bajo_detalle", productosStockBajo.stream().map(ps -> {
            Map<String, Object> detalle = new HashMap<>();
            detalle.put("producto", ps.getProducto().getNombre());
            detalle.put("sucursal", ps.getSucursal().getNombre());
            detalle.put("stock", ps.getStock());
            return detalle;
        }).toList());
        
        return status;
    }
}