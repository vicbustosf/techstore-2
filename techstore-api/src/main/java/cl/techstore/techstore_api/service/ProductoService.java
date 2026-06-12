package cl.techstore.techstore_api.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cl.techstore.techstore_api.dto.ProductoDTO;
import cl.techstore.techstore_api.model.Producto;
import cl.techstore.techstore_api.repository.ProductoRepository;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> listarTodos() {
    return productoRepository.findByActivoTrue();
    }

    public Producto crear(ProductoDTO dto) {
        Producto producto = new Producto();

        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setCategoria(dto.getCategoria());

        if (dto.getActivo() == null) {
            producto.setActivo(true);
        } else {
            producto.setActivo(dto.getActivo());
        }

        return productoRepository.save(producto);
    }

    public Producto modificar(Long id, ProductoDTO dto) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setCategoria(dto.getCategoria());

        if (dto.getActivo() != null) {
            producto.setActivo(dto.getActivo());
        }

        return productoRepository.save(producto);
    }

    public void eliminar(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        producto.setActivo(false);
        productoRepository.save(producto);
    }
}
