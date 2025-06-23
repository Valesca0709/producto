package ProductoMicros.producto.controller;

import ProductoMicros.producto.service.TransbankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class PagoController {

    @Autowired
    private TransbankService transbankService;

    @GetMapping("/")
    @ResponseBody
    public String inicio() {
        return "<a href='/pagar'>Iniciar pago</a>";
    }

    @GetMapping("/pagar")
@ResponseBody
public Map<String, String> pagar(@RequestParam int idProductoSucursal, @RequestParam int cantidad) {
    System.out.println(">>> Entrando al método /pagar con idProductoSucursal=" + idProductoSucursal + " y cantidad=" + cantidad);

    double total = productoSucursalService.calcularTotal(idProductoSucursal, cantidad);
    String returnUrl = "http://localhost:8087/retorno";

    Map<String, Object> respuesta = transbankService.iniciarTransaccion(
            (int) total,
            "ordenCompra_" + UUID.randomUUID(),
            "session_" + UUID.randomUUID(),
            returnUrl
    );

    Map<String, String> response = new HashMap<>();
    if (respuesta.containsKey("token") && respuesta.containsKey("url")) {
        String token = (String) respuesta.get("token");
        String url = (String) respuesta.get("url");
        System.out.println(">>> Redirigiendo a: " + url + "?token_ws=" + token);
        response.put("token", token);
        response.put("url", url);
    } else {
        System.out.println(">>> Error al iniciar transacción con Transbank");
        response.put("error", "No se pudo iniciar la transacción");
    }

    return response;
    }


    @PostMapping("/retorno")
    @ResponseBody
    public Map<String, Object> retorno(@RequestParam("token_ws") String token) {
        return transbankService.confirmarTransaccion(token);
    }
}
