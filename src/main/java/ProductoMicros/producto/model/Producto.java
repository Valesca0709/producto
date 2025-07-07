package ProductoMicros.producto.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    private int idProducto;

    @Column(length = 50, nullable = false)
    private String nombre;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private double precio;

    @OneToMany(mappedBy = "producto")
    private List<DetalleVenta> detalleVenta;
}
