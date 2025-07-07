package ProductoMicros.producto.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ProductoMicros.producto.model.Producto;
import ProductoMicros.producto.repository.ProductoRepository;

import java.util.List;
import java.util.Optional;


@Service
public class ProductoService {
    @Autowired
    private ProductoRepository productoRepository;
    
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }
    
    public Optional<Producto> findById(int id) {
        return productoRepository.findById(id);
    }
    
    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }
    
    public void deleteById(int id) {
        productoRepository.deleteById(id);
    }
    
    // ✅ CORRECCIÓN: Se elimina el método updateStock que hacía referencia
    // a un campo que ya no existe en la entidad Producto.
}