package ProductoMicros.producto.controller;

import ProductoMicros.producto.model.ProductoSucursal;
import ProductoMicros.producto.service.ProductoSucursalService;
import ProductoMicros.producto.service.TransbankService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class PagoController {

    @Autowired
    private TransbankService transbankService;

    @Autowired
    private ProductoSucursalService productoSucursalService;

    // Endpoint para manejar el pago de un carrito completo.
    @PostMapping("/iniciar-pago-carrito")
    @ResponseBody
    public ResponseEntity<Map<String, String>> pagarCarrito(@RequestBody List<Map<String, Object>> cart) {
        if (cart == null || cart.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El carrito está vacío."));
        }

        double totalAmount = 0;
        // Validación de stock y cálculo del total
        for (Map<String, Object> item : cart) {
            int id = (int) item.get("id");
            int quantity = (int) item.get("quantity");
            ProductoSucursal ps = productoSucursalService.getById(id);
            if (ps.getStock() < quantity) {
                return ResponseEntity.badRequest().body(Map.of("error", "Stock insuficiente para el producto: " + ps.getProducto().getNombre()));
            }
            totalAmount += ps.getPrecio_unitario() * quantity;
        }
        
        String buyOrder = "cart-orden_" + UUID.randomUUID().toString().substring(0, 8);
        String sessionId = "cart-session_" + UUID.randomUUID().toString().substring(0, 8);
        String returnUrl = "http://localhost:8087/retorno";

        Map<String, Object> respuesta = transbankService.iniciarTransaccion((int) Math.round(totalAmount), buyOrder, sessionId, returnUrl, cart);

        if (respuesta.containsKey("token") && respuesta.containsKey("url")) {
            return ResponseEntity.ok(Map.of("token", (String) respuesta.get("token"), "url", (String) respuesta.get("url")));
        } else {
            return ResponseEntity.status(500).body(Map.of("error", "No se pudo iniciar la transacción con Transbank."));
        }
    }

    // El método de retorno ahora procesa el carrito guardado.
    @RequestMapping(value = "/retorno", method = {RequestMethod.GET, RequestMethod.POST})
    public String retorno(@RequestParam(value = "token_ws", required = false) String token, Model model) {
        if (token == null) {
            return "compra_rechazada";
        }

        Map<String, Object> resultado = transbankService.confirmarTransaccion(token);

        if ("AUTHORIZED".equalsIgnoreCase((String) resultado.get("status"))) {
            List<Map<String, Object>> cart = (List<Map<String, Object>>) resultado.get("cart");
            if (cart != null) {
                for (Map<String, Object> item : cart) {
                    try {
                        int idProductoSucursal = (int) item.get("id");
                        int cantidadComprada = (int) item.get("quantity");
                        productoSucursalService.updateStock(idProductoSucursal, cantidadComprada);
                        System.out.println("✅ Stock actualizado para ProductoSucursal ID " + idProductoSucursal);
                    } catch (Exception e) {
                        System.err.println("❌ Error al actualizar stock para el item " + item.get("id") + ": " + e.getMessage());
                    }
                }
            }

            // Prepara los datos para la vista de éxito
            String productosComprados = cart.stream()
                .map(item -> (String) item.get("name") + " (x" + item.get("quantity") + ")")
                .collect(Collectors.joining(", "));

            model.addAttribute("producto", productosComprados);
            model.addAttribute("total", resultado.get("amount"));
            model.addAttribute("fecha", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
            return "compra_exitosa";
        } else {
            return "compra_rechazada";
        }
    }
}