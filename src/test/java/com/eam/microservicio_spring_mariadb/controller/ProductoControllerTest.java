package com.eam.microservicio_spring_mariadb.controller;

import com.eam.microservicio_spring_mariadb.entity.Producto;
import com.eam.microservicio_spring_mariadb.service.ProductoService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(ProductoController.class)
public class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void crearProducto_DeberiaRetornarProductoCreado() throws Exception {
        Producto producto = new Producto(1L, "P001", "Camisa", 50000.0, 10);

        when(productoService.crearProducto(any())).thenReturn(producto);

        mockMvc.perform(post("/api/productos/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(producto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("P001"));
    }

    @Test
    void crearProducto_DeberiaRetornarBadRequestSiFalla() throws Exception {
        Producto producto = new Producto(null, "P001", "Camisa", 50000.0, 10);

        when(productoService.crearProducto(any())).thenThrow(new IllegalArgumentException("Código duplicado"));

        mockMvc.perform(post("/api/productos/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(producto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Código duplicado"));
    }

    @Test
    void listarProductos_DeberiaRetornarLista() throws Exception {
        List<Producto> lista = List.of(
                new Producto(1L, "P001", "Camisa", 50000.0, 10)
        );

        when(productoService.listarProductos()).thenReturn(lista);

        mockMvc.perform(get("/api/productos/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("P001"));
    }

    @Test
    void listarProductos_DeberiaRetornarNotFoundSiNoHay() throws Exception {
        when(productoService.listarProductos()).thenThrow(new IllegalStateException("No hay productos"));

        mockMvc.perform(get("/api/productos/listar"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No hay productos"));
    }

    @Test
    void obtenerProducto_DeberiaRetornarProductoSiExiste() throws Exception {
        Producto producto = new Producto(1L, "P001", "Camisa", 50000.0, 10);

        when(productoService.obtenerPorCodigo("P001")).thenReturn(producto);

        mockMvc.perform(get("/api/productos/obtener/P001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("P001"));
    }

    @Test
    void obtenerProducto_DeberiaRetornarNotFoundSiNoExiste() throws Exception {
        when(productoService.obtenerPorCodigo("P999"))
                .thenThrow(new NoSuchElementException("No existe"));

        mockMvc.perform(get("/api/productos/obtener/P999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No existe"));
    }

    @Test
    void actualizarProducto_DeberiaActualizarYRetornarOk() throws Exception {
        Producto producto = new Producto(1L, "P001", "Camisa actualizada", 60000.0, 8);

        when(productoService.actualizarProducto(eq("P001"), any()))
                .thenReturn(producto);

        mockMvc.perform(put("/api/productos/actualizar/P001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(producto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Camisa actualizada"));
    }

    @Test
    void actualizarProducto_DeberiaRetornarBadRequestSiError() throws Exception {
        Producto producto = new Producto(1L, "P002", "Camisa", 50000.0, 10);

        when(productoService.actualizarProducto(eq("P001"), any()))
                .thenThrow(new IllegalArgumentException("No se permite modificar el código"));

        mockMvc.perform(put("/api/productos/actualizar/P001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(producto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No se permite modificar el código"));
    }

    @Test
    void eliminarProducto_DeberiaRetornarOk() throws Exception {
        doNothing().when(productoService).eliminarProducto("P001");

        mockMvc.perform(delete("/api/productos/eliminar/P001"))
                .andExpect(status().isOk())
                .andExpect(content().string("Producto eliminado exitosamente."));
    }

    @Test
    void eliminarProducto_DeberiaRetornarNotFoundSiNoExiste() throws Exception {
        doThrow(new NoSuchElementException("No se encontró el producto")).when(productoService).eliminarProducto("P001");

        mockMvc.perform(delete("/api/productos/eliminar/P001"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No se encontró el producto"));
    }
}
