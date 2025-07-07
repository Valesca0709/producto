package ProductoMicros.producto.Grpc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProductoGrpcController {
    @Autowired
    private ProductoGrpcCliente grpcClient;

    @PostMapping("/producto/guardar")
    public String guardarProducto(@RequestParam int idProducto,
                                   @RequestParam String nombre,
                                   @RequestParam double precio,
                                   Model model) {
        String mensaje = grpcClient.agregarProducto(idProducto, nombre, precio);
        model.addAttribute("resultado", mensaje);
        return "resultado";
    }

    @GetMapping("/producto/formulario")
    public String formularioGrpc() {
        return "agregar_producto";
    }
}
