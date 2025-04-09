package com.eam.microservicio_spring_mariadb.service;

import com.eam.microservicio_spring_mariadb.entity.Producto;
import com.eam.microservicio_spring_mariadb.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    // Crear producto (solo si el código no existe)
    public Producto crearProducto(Producto producto) {
        if (productoRepository.existsByCodigo(producto.getCodigo())) {
            throw new RuntimeException("Ya existe un producto con el código: " + producto.getCodigo());
        }
        return productoRepository.save(producto);
    }

    // Obtener todos los productos
    public List<Producto> listarProductos() {
        List<Producto> productos = productoRepository.findAll();
        if (productos.isEmpty()) {
            throw new RuntimeException("No hay productos registrados en la base de datos.");
        }
        return productos;
    }

    // Obtener un producto por su código (con validación si no existe)
    public Producto obtenerPorCodigo(String codigo) {
        return productoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("No se encontró un producto con el código: " + codigo));
    }


    // Actualizar producto por código (sin permitir cambiar el código)
    public Producto actualizarProducto(String codigo, Producto nuevoProducto) {
        Optional<Producto> productoExistente = productoRepository.findByCodigo(codigo);

        if (productoExistente.isEmpty()) {
            throw new RuntimeException("No se encontró un producto con el código: " + codigo);
        }

        if (!codigo.equals(nuevoProducto.getCodigo())) {
            throw new RuntimeException("No se permite modificar el código del producto.");
        }

        Producto producto = productoExistente.get();
        producto.setNombre(nuevoProducto.getNombre());
        producto.setPrecio(nuevoProducto.getPrecio());
        producto.setCantidad(nuevoProducto.getCantidad());

        return productoRepository.save(producto);
    }

    // Eliminar producto por código
    public void eliminarProducto(String codigo) {
        if (!productoRepository.existsByCodigo(codigo)) {
            throw new RuntimeException("No se puede eliminar. No existe producto con el código: " + codigo);
        }
        productoRepository.deleteByCodigo(codigo);
    }
}
