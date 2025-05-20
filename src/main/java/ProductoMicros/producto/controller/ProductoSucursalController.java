package ProductoMicros.producto.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ProductoMicros.producto.model.ProductoSucursal;
import ProductoMicros.producto.service.ProductoSucursalService;

@RestController
@RequestMapping("/api/v1/productoSucursal")
public class ProductoSucursalController {
    @Autowired
    private ProductoSucursalService productoSucursalService;


    //Listar sucursal en el que aparece el producto y la sucursal en el que se encuentra el producto
    @GetMapping
    public ResponseEntity<List<ProductoSucursal>> getAllProductosSucursal() {
        List<ProductoSucursal> productos = productoSucursalService.findAll();
        if (productos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }

    // Buscar por nombre o id el producto
    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoSucursal>> buscarPorNombreYId(
            @RequestParam String nombre, 
            @RequestParam int idProducto) {
        
        List<ProductoSucursal> resultados = productoSucursalService.findByNombreAndId(nombre, idProducto);
        
        if (resultados.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(resultados);
    }
    
    @GetMapping("/calcular-total")
    public ResponseEntity<Double> calcularTotal(
            @RequestParam int idProductoSucursal, 
            @RequestParam int cantidad) {
        
        try {
            double total = productoSucursalService.calcularTotal(idProductoSucursal, cantidad);
            return ResponseEntity.ok(total);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
