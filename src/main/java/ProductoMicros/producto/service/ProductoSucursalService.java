package ProductoMicros.producto.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ProductoMicros.producto.model.ProductoSucursal;
import ProductoMicros.producto.repository.ProductoSucursalRepository;

@Service
public class ProductoSucursalService {
    @Autowired
    private ProductoSucursalRepository productoSucursalRepository;

    public List<ProductoSucursal> findAll() {
        // Se llama al método del repositorio para obtener los datos.
        List<ProductoSucursal> productos = productoSucursalRepository.findAllWithStock();

        // ✅ --- INICIO DE CÓDIGO PARA DEPURACIÓN ---
        System.out.println("==========================================================");
        System.out.println("DEPURANDO: Verificando datos de la base de datos...");
        
        if (productos.isEmpty()) {
            System.out.println("RESULTADO: La consulta no devolvió ningún producto con stock > 0.");
        } else {
            System.out.println("RESULTADO: Se encontraron " + productos.size() + " productos con stock.");
            for (ProductoSucursal ps : productos) {
                // Imprime los detalles de cada producto encontrado.
                System.out.println(
                    "  -> ID: " + ps.getIdProductoSucursal() + 
                    ", Producto: " + ps.getProducto().getNombre() + 
                    ", Sucursal: " + ps.getSucursal().getNombre() + 
                    ", Stock: " + ps.getStock()
                );
            }
        }
        System.out.println("==========================================================");
        // ✅ --- FIN DE CÓDIGO PARA DEPURACIÓN ---

        return productos;
    }   

    // ... (El resto del archivo no cambia)

    public List<ProductoSucursal> findByNombreAndId(String nombre, int idProducto) {
        return productoSucursalRepository.findByProductoNombreAndProductoIdProducto(nombre, idProducto);
    }

    public double calcularTotal(int idProductoSucursal, int cantidad) {
        ProductoSucursal productoSucursal = productoSucursalRepository.findByIdProductoSucursal(idProductoSucursal);
        if (productoSucursal == null) {
            throw new IllegalArgumentException("ProductoSucursal no encontrado con ID: " + idProductoSucursal);
        }
        return productoSucursal.getPrecio_unitario() * cantidad;
    }

    public ProductoSucursal getById(int idProductoSucursal) {
        ProductoSucursal ps = productoSucursalRepository.findByIdProductoSucursal(idProductoSucursal);
        if (ps == null) {
            throw new IllegalArgumentException("ProductoSucursal no encontrado con ID: " + idProductoSucursal);
        }
        return ps;
    }
    
    @Transactional
    public ProductoSucursal updateStock(int idProductoSucursal, int cantidadComprada) {
        ProductoSucursal productoSucursal = getById(idProductoSucursal);
        if (productoSucursal.getStock() < cantidadComprada) {
            throw new IllegalStateException("Stock insuficiente");
        }
        int nuevoStock = productoSucursal.getStock() - cantidadComprada;
        productoSucursal.setStock(nuevoStock);
        return productoSucursalRepository.save(productoSucursal);
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