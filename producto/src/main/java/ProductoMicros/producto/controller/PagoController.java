package ProductoMicros.producto.controller;

import ProductoMicros.producto.service.TransbankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

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
    public RedirectView pagar() {
        System.out.println(">>> Entrando al método /pagar");

        String returnUrl = "http://localhost:8087/retorno";
        Map<String, Object> respuesta = transbankService.iniciarTransaccion(
                10000, // monto ficticio
                "ordenCompra12345678",
                "session123",
                returnUrl
        );

        System.out.println("RESPUESTA: " + respuesta);

        if (respuesta.containsKey("token") && respuesta.containsKey("url")) {
            String token = (String) respuesta.get("token");
            String url = (String) respuesta.get("url");
            System.out.println(">>> Redirigiendo a: " + url + "?token_ws=" + token);
            return new RedirectView(url + "?token_ws=" + token);
        } else {
            return new RedirectView("/error-transbank");
        }
    }

    @PostMapping("/retorno")
    @ResponseBody
    public Map<String, Object> retorno(@RequestParam("token_ws") String token) {
        return transbankService.confirmarTransaccion(token);
    }
}
