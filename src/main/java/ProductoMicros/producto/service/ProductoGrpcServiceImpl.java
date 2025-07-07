package ProductoMicros.producto.service;

import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import ProductoMicros.producto.model.Producto;
import ProductoMicros.producto.repository.ProductoRepository;
import io.grpc.stub.StreamObserver;

import com.grpc.producto.NuevoProductoRequest;
import com.grpc.producto.ProductoResponse;
import com.grpc.producto.ProductoServiceGrpc;

@GrpcService
public class ProductoGrpcServiceImpl extends ProductoServiceGrpc.ProductoServiceImplBase {

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public void agregarProducto(NuevoProductoRequest request, StreamObserver<ProductoResponse> responseObserver) {
        System.out.println("Producto recibido: " + request.getIdProducto() + " - " + request.getNombre()+ " - " + request.getPrecio());

        // Guardar en base de datos
        Producto producto = new Producto();
        producto.setIdProducto(request.getIdProducto());
        producto.setNombre(request.getNombre());
        producto.setPrecio(request.getPrecio());

        productoRepository.save(producto);

        ProductoResponse response = ProductoResponse.newBuilder()
                .setMensaje("✅ Producto guardado correctamente en la base de datos")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}