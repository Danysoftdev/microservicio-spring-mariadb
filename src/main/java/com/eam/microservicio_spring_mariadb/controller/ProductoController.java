package com.eam.microservicio_spring_mariadb.controller;

import com.eam.microservicio_spring_mariadb.entity.Producto;
import com.eam.microservicio_spring_mariadb.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // POST: /api/productos/crear
    @PostMapping("/crear")
    public ResponseEntity<?> crearProducto(@Valid @RequestBody Producto producto) {
        try {
            Producto productoCreado = productoService.crearProducto(producto);
            return ResponseEntity.ok(productoCreado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET: /api/productos/listar
    @GetMapping("/listar")
    public ResponseEntity<?> listarProductos() {
        try {
            List<Producto> productos = productoService.listarProductos();
            return ResponseEntity.ok(productos);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // GET: /api/productos/obtener/{codigo}
    @GetMapping("/obtener/{codigo}")
    public ResponseEntity<?> obtenerProducto(@PathVariable String codigo) {
        try {
            Producto producto = productoService.obtenerPorCodigo(codigo);
            return ResponseEntity.ok(producto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // PUT: /api/productos/actualizar/{codigo}
    @PutMapping("/actualizar/{codigo}")
    public ResponseEntity<?> actualizarProducto(@PathVariable String codigo, @Valid @RequestBody Producto producto) {
        try {
            Producto productoActualizado = productoService.actualizarProducto(codigo, producto);
            return ResponseEntity.ok(productoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DELETE: /api/productos/eliminar/{codigo}
    @DeleteMapping("/eliminar/{codigo}")
    public ResponseEntity<?> eliminarProducto(@PathVariable String codigo) {
        try {
            productoService.eliminarProducto(codigo);
            return ResponseEntity.ok("Producto eliminado exitosamente.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
