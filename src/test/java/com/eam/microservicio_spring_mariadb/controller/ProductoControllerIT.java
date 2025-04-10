package com.eam.microservicio_spring_mariadb.controller;

import com.eam.microservicio_spring_mariadb.entity.Producto;
import com.eam.microservicio_spring_mariadb.repository.ProductoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class ProductoControllerIT {

    @Container
    static MariaDBContainer<?> mariaDBContainer = new MariaDBContainer<>("mariadb:10.6.4")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mariaDBContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mariaDBContainer::getUsername);
        registry.add("spring.datasource.password", mariaDBContainer::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Producto producto;

    @Autowired
    private ProductoRepository productoRepository;

    @BeforeEach
    void cleanDB() {
        productoRepository.deleteAll();
    }

    @BeforeEach
    void setUp() {
        producto = new Producto(null, "P001", "Camisa", 50000.0, 10);
    }

    @Test
    void crearProducto_DeberiaRetornar200() throws Exception {
        mockMvc.perform(post("/api/productos/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("P001"));
    }

    @Test
    void crearProducto_Duplicado_DeberiaRetornar400() throws Exception {
        mockMvc.perform(post("/api/productos/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/productos/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Ya existe un producto")));
    }

    @Test
    void listarProductos_DeberiaRetornar200() throws Exception {
        // Primero creamos un producto
        mockMvc.perform(post("/api/productos/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isOk());

        // Ahora sí podemos listar
        mockMvc.perform(get("/api/productos/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("P001"));
    }


    @Test
    void obtenerProducto_Existente_DeberiaRetornar200() throws Exception {
        mockMvc.perform(post("/api/productos/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/productos/obtener/P001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Camisa"));
    }

    @Test
    void obtenerProducto_NoExistente_DeberiaRetornar404() throws Exception {
        mockMvc.perform(get("/api/productos/obtener/NO_EXISTE"))
                .andExpect(status().isNotFound());
    }

    @Test
    void actualizarProducto_DeberiaRetornar200() throws Exception {
        mockMvc.perform(post("/api/productos/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isOk());

        producto.setNombre("Camisa actualizada");
        producto.setPrecio(55000.0);
        producto.setCantidad(15);

        mockMvc.perform(put("/api/productos/actualizar/P001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Camisa actualizada"));
    }

    @Test
    void actualizarProducto_CodigoDiferente_DeberiaRetornar400() throws Exception {
        producto.setCodigo("OTRO");

        mockMvc.perform(put("/api/productos/actualizar/P001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("No se permite modificar el código del producto")));
    }

    @Test
    void eliminarProducto_Existente_DeberiaRetornar200() throws Exception {
        mockMvc.perform(post("/api/productos/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/productos/eliminar/P001"))
                .andExpect(status().isOk())
                .andExpect(content().string("Producto eliminado exitosamente."));
    }

    @Test
    void eliminarProducto_NoExistente_DeberiaRetornar404() throws Exception {
        mockMvc.perform(delete("/api/productos/eliminar/NO_EXISTE"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listarProductos_SinDatos_DeberiaRetornar404() throws Exception {
        mockMvc.perform(delete("/api/productos/eliminar/P001"));

        mockMvc.perform(get("/api/productos/listar"))
                .andExpect(status().isNotFound());
    }
}
