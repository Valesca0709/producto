package ProductoMicros.producto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index"; // Muestra la página de ventas principal
    }

    //  Este método servirá la nueva página de monitoreo SSE.
    @GetMapping("/sse-monitor")
    public String sseMonitor() {
        return "sse_monitor"; // Apunta al nuevo archivo sse_monitor.html
    }
}