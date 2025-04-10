package com.eam.microservicio_spring_mariadb.service;

import com.eam.microservicio_spring_mariadb.entity.Producto;
import com.eam.microservicio_spring_mariadb.repository.ProductoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    // Crear producto (solo si el código no existe)
    public Producto crearProducto(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }

        if (productoRepository.existsByCodigo(producto.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un producto con el mismo código");
        }
        return productoRepository.save(producto);
    }

    // Obtener todos los productos
    public List<Producto> listarProductos() {
        List<Producto> productos = productoRepository.findAll();
        if (productos.isEmpty()) {
            throw new IllegalStateException("No hay productos registrados en la base de datos");
        }
        return productos;
    }

    // Obtener un producto por su código (con validación si no existe)
    public Producto obtenerPorCodigo(String codigo) {
        return productoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el producto con el código proporcionado"));
    }


    // Actualizar producto por código (sin permitir cambiar el código)
    public Producto actualizarProducto(String codigo, Producto nuevoProducto) {
        Optional<Producto> productoExistente = productoRepository.findByCodigo(codigo);

        if (!codigo.equals(nuevoProducto.getCodigo())) {
            throw new IllegalArgumentException("No se permite modificar el código del producto");
        }

        if (productoExistente.isEmpty()) {
            throw new NoSuchElementException("No se encontró el producto con el código proporcionado");
        }

        Producto producto = productoExistente.get();
        producto.setNombre(nuevoProducto.getNombre());
        producto.setPrecio(nuevoProducto.getPrecio());
        producto.setCantidad(nuevoProducto.getCantidad());

        return productoRepository.save(producto);
    }

    // Eliminar producto por código
    public void eliminarProducto(String codigo) {
        Producto producto = productoRepository.findByCodigo(codigo)
            .orElseThrow(() -> new NoSuchElementException("No se encontró el producto con el código proporcionado"));

        productoRepository.delete(producto);
    }
}
