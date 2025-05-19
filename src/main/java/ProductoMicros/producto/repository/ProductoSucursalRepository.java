package ProductoMicros.producto.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ProductoMicros.producto.model.ProductoSucursal;

@Repository
public interface ProductoSucursalRepository extends JpaRepository<ProductoSucursal, Integer> {
     
   List<ProductoSucursal> findByProductoNombreAndProductoIdProducto(String nombre, int idProducto);
   ProductoSucursal findByIdProductoSucursal(int idProductoSucursal);
} 
    