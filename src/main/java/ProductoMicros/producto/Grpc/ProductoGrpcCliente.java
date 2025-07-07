package ProductoMicros.producto.Grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Component;

import com.grpc.producto.NuevoProductoRequest;
import com.grpc.producto.ProductoResponse;
import com.grpc.producto.ProductoServiceGrpc;


@Component
public class ProductoGrpcCliente {
     private final ProductoServiceGrpc.ProductoServiceBlockingStub stub;

    public ProductoGrpcCliente() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9092)
                .usePlaintext()
                .build();
        stub = ProductoServiceGrpc.newBlockingStub(channel);
    }

    public String agregarProducto(int idProducto, String nombre, double precio) {
    NuevoProductoRequest request = NuevoProductoRequest.newBuilder()
                .setIdProducto(idProducto)
                .setNombre(nombre)
                .setPrecio(precio)
                .build();


    ProductoResponse response = stub.agregarProducto(request);
      return response.getMensaje();
    }
}

