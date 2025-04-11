package com.eam.microservicio_spring_mariadb.controller;

import com.eam.microservicio_spring_mariadb.entity.Producto;
import com.eam.microservicio_spring_mariadb.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @PostMapping("/crear")
    public ResponseEntity<?> crearProducto(@Valid @RequestBody Producto producto) {
        try {
            Producto productoCreado = productoService.crearProducto(producto);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Producto creado exitosamente.");
            response.put("producto", productoCreado);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<?> listarProductos() {
        try {
            List<Producto> productos = productoService.listarProductos();

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Lista de productos obtenida correctamente.");
            response.put("productos", productos);

            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/obtener/{codigo}")
    public ResponseEntity<?> obtenerProducto(@PathVariable String codigo) {
        try {
            Producto producto = productoService.obtenerPorCodigo(codigo);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Producto obtenido exitosamente.");
            response.put("producto", producto);

            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PutMapping("/actualizar/{codigo}")
    public ResponseEntity<?> actualizarProducto(@PathVariable String codigo, @Valid @RequestBody Producto producto) {
        try {
            Producto productoActualizado = productoService.actualizarProducto(codigo, producto);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Producto actualizado exitosamente.");
            response.put("producto", productoActualizado);

            return ResponseEntity.ok(response);
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

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Producto eliminado exitosamente.");
            response.put("codigo", codigo);

            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
