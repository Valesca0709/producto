package ProductoMicros.producto.controller;

import ProductoMicros.producto.model.ProductoSucursal;
import ProductoMicros.producto.service.ProductoSucursalService;
import ProductoMicros.producto.service.TransbankService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class PagoController {

    @Autowired
    private TransbankService transbankService;
    @Autowired
    private ProductoSucursalService productoSucursalService;

    @GetMapping("/pago")
    @ResponseBody
    public String inicio() {
        return "<a href='/pagar'>Iniciar pago</a>";
    }

    @GetMapping("/pagar")
    @ResponseBody
    public Map<String, String> pagar(@RequestParam int idProductoSucursal, @RequestParam int cantidad) {
        System.out.println(">>> Entrando al método /pagar con idProductoSucursal=" + idProductoSucursal + " y cantidad=" + cantidad);

        double total = productoSucursalService.calcularTotal(idProductoSucursal, cantidad);
        int montoFinal = (int) Math.round(total);  // ✅ CORRECCIÓN: conversión segura

        System.out.println(">>> Monto final enviado a Transbank: " + montoFinal);

        String returnUrl = "http://localhost:8087/retorno";

        ProductoSucursal productoSucursal = productoSucursalService.getById(idProductoSucursal);
        String nombreProducto = productoSucursal.getProducto().getNombre(); // Ajusta según tu modelo

        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        String buyOrder = "orden_" + uuid;

        Map<String, Object> respuesta = transbankService.iniciarTransaccion(
            montoFinal,
            buyOrder,
            "session_" + UUID.randomUUID(),
            returnUrl,
            nombreProducto,
            cantidad
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

@RequestMapping(value = "/retorno", method = {RequestMethod.GET, RequestMethod.POST})
public String retorno(@RequestParam("token_ws") String token, Model model) {
    Map<String, Object> resultado = transbankService.confirmarTransaccion(token);

    if ("AUTHORIZED".equalsIgnoreCase((String) resultado.get("status"))) {
        model.addAttribute("producto", resultado.get("nombreProducto"));  // Asegúrate que esto esté seteado en TransbankService
        model.addAttribute("cantidad", resultado.get("cantidad"));
        model.addAttribute("total", resultado.get("monto"));
        model.addAttribute("fecha", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        return "compra_exitosa";
    } else {
        return "compra_rechazada";
    }
}



}
