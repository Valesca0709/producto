package ProductoMicros.producto.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "producto")
public class Producto {
   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idProducto;

    @Column(length = 50, unique = true, nullable = false)
    private String marca;
    
    @Column(length = 50, nullable = false)
    private String nombre;
    
    @Column(length = 150, nullable = false)
    private String descripcion;
    
    @Column(nullable = false)
    private int stock;

    @OneToMany(mappedBy = "producto")
    private List<DetalleVenta> detalleVenta;
}

