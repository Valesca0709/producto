package ProductoMicros.producto.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private double precio;

    @OneToMany(mappedBy = "producto")
    @JsonIgnore // Evita recursividad en las respuestas JSON
    private List<ProductoSucursal> productoSucursales;
    
    @OneToMany(mappedBy = "producto")
    @JsonIgnore
    private List<DetalleVenta> detalleVenta;
}