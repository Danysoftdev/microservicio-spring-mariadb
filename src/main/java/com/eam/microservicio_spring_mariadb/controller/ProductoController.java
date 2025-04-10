package com.eam.microservicio_spring_mariadb.controller;

import com.eam.microservicio_spring_mariadb.entity.Producto;
import com.eam.microservicio_spring_mariadb.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @PostMapping("/crear")
    public ResponseEntity<?> crearProducto(@Valid @RequestBody Producto producto) {
        try {
            Producto productoCreado = productoService.crearProducto(producto);
            return ResponseEntity.ok(productoCreado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<?> listarProductos() {
        try {
            List<Producto> productos = productoService.listarProductos();
            return ResponseEntity.ok(productos);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/obtener/{codigo}")
    public ResponseEntity<?> obtenerProducto(@PathVariable String codigo) {
        try {
            Producto producto = productoService.obtenerPorCodigo(codigo);
            return ResponseEntity.ok(producto);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PutMapping("/actualizar/{codigo}")
    public ResponseEntity<?> actualizarProducto(@PathVariable String codigo, @Valid @RequestBody Producto producto) {
        try {
            Producto productoActualizado = productoService.actualizarProducto(codigo, producto);
            return ResponseEntity.ok(productoActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @DeleteMapping("/eliminar/{codigo}")
    public ResponseEntity<?> eliminarProducto(@PathVariable String codigo) {
        try {
            productoService.eliminarProducto(codigo);
            return ResponseEntity.ok("Producto eliminado exitosamente.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
