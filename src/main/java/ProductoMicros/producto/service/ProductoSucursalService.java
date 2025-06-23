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

    public List<ProductoSucursal> obtenerProductoSucursal() {
    return productoSucursalRepository.findAll();
    }


    public double calcularTotal(int idProductoSucursal, int cantidad) {
        ProductoSucursal productoSucursal = productoSucursalRepository.findByIdProductoSucursal(idProductoSucursal);
        
        if (productoSucursal == null) {
            throw new IllegalArgumentException("ProductoSucursal no encontrado con ID: " + idProductoSucursal);
        }
        
        return productoSucursal.getPrecio_unitario() * cantidad;
    }
}
