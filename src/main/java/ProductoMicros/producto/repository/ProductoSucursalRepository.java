package ProductoMicros.producto.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; 
import org.springframework.stereotype.Repository;

import ProductoMicros.producto.model.ProductoSucursal;

@Repository
public interface ProductoSucursalRepository extends JpaRepository<ProductoSucursal, Integer> {
     
   List<ProductoSucursal> findByProductoNombreAndProductoIdProducto(String nombre, int idProducto);
   
   ProductoSucursal findByIdProductoSucursal(int idProductoSucursal);

   // Reemplazamos el método anterior por una consulta explícita
   // que une las tablas de producto y sucursal. Esto asegura que todos los
   // datos necesarios se carguen en una sola consulta, resolviendo el problema.
   @Query("SELECT ps FROM ProductoSucursal ps JOIN FETCH ps.producto JOIN FETCH ps.sucursal WHERE ps.stock > 0")
   List<ProductoSucursal> findAllWithStock();
   
   // Consulta para obtener productos con stock bajo (menor a 10 unidades)
   @Query("SELECT ps FROM ProductoSucursal ps JOIN FETCH ps.producto JOIN FETCH ps.sucursal WHERE ps.stock < 10 AND ps.stock > 0")
   List<ProductoSucursal> findProductosConStockBajo();
}