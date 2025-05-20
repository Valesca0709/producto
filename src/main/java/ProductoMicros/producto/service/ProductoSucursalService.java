package ProductoMicros.producto.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ProductoMicros.producto.model.ProductoSucursal;
import ProductoMicros.producto.repository.ProductoSucursalRepository;

@Service
public class ProductoSucursalService {
    @Autowired
    private ProductoSucursalRepository productoSucursalRepository;

    public List<ProductoSucursal> findAll() {
        return productoSucursalRepository.findAll();
    }   

    public List<ProductoSucursal> findByNombreAndId(String nombre, int idProducto) {
        return productoSucursalRepository.findByProductoNombreAndProductoIdProducto(nombre, idProducto);
    }


    public double calcularTotal(int idProductoSucursal, int cantidad) {
    ProductoSucursal productoSucursal = productoSucursalRepository.findByIdProductoSucursal(idProductoSucursal);
    
    if (productoSucursal == null) {
        throw new IllegalArgumentException("ProductoSucursal no encontrado con ID: " + idProductoSucursal);
    }

    double total = productoSucursal.getPrecio_unitario() * cantidad;
    System.out.println(">>> Precio unitario: " + productoSucursal.getPrecio_unitario());
    System.out.println(">>> Cantidad: " + cantidad);
    System.out.println(">>> Total calculado: " + total);

    return total;
}

public ProductoSucursal getById(int idProductoSucursal) {
    ProductoSucursal ps = productoSucursalRepository.findByIdProductoSucursal(idProductoSucursal);
    if (ps == null) {
        throw new IllegalArgumentException("ProductoSucursal no encontrado con ID: " + idProductoSucursal);
    }
    return ps;
}

}
