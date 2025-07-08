package ProductoMicros.producto.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "producto_sucursal")
public class ProductoSucursal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idProductoSucursal;
    
    @ManyToOne
    @JoinColumn(name = "idProducto", nullable = false)
    private Producto producto;
    
    @ManyToOne
    @JoinColumn(name = "id_sucursal", nullable = false)
    private Sucursal sucursal;
    
    // Se especifica el nombre de la columna en la BD.
    // Esto asegura que Java encuentre la columna correcta, sin importar
    // si se llama 'stock' o 'cantidad' en la base de datos.
    @Column(name = "stock", nullable = false)
    private int stock;
    
    @Column(nullable = false)
    private double precio_unitario;
}