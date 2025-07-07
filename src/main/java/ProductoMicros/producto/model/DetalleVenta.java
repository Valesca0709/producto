package ProductoMicros.producto.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "detalle_venta")
public class DetalleVenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_detalle_venta;
    
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;
    
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "id_sucursal", nullable = false)
    private Sucursal sucursal;
    
    @Column(nullable = false)
    private int cantidad;
    
    @Column(nullable = false)
    private double precio_unitario;

     
     @Column(nullable = false)
    private double totalVenta;

    @Column(nullable = false)
    private double iva_porcentaje;
    
    @Column(nullable = false)
    private LocalDateTime fecha_venta;

}
