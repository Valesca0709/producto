package ProductoMicros.producto.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ProductoMicros.producto.model.ProductoSucursal;
import ProductoMicros.producto.repository.ProductoSucursalRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

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

public double obtenerTasaUSD() throws IOException {
    String apiUrl = "https://mindicador.cl/api/dolar";
    HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
    conn.setRequestMethod("GET");

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
        StringBuilder json = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            json.append(line);
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json.toString());
        return root.get("serie").get(0).get("valor").asDouble();
    }
}

}
