package ProductoMicros.producto.controller; // Ajusta el nombre del paquete

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index"; // Indica el nombre de la plantilla a renderizar
    }
}