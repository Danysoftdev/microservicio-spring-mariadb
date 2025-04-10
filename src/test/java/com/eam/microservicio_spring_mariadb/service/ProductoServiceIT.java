package com.eam.microservicio_spring_mariadb.service;

import com.eam.microservicio_spring_mariadb.entity.Producto;
import com.eam.microservicio_spring_mariadb.repository.ProductoRepository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductoServiceIT {

    @Container
    static MariaDBContainer<?> mariadb = new MariaDBContainer<>("mariadb:10.6")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mariadb::getJdbcUrl);
        registry.add("spring.datasource.username", mariadb::getUsername);
        registry.add("spring.datasource.password", mariadb::getPassword);
    }

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProductoRepository productoRepository;

    @BeforeEach
    void cleanDB() {
        productoRepository.deleteAll();
    }

    // 1. Crear producto
    @Test
    @Order(1)
    void crearProducto_DeberiaGuardarCorrectamente() {
        Producto producto = new Producto(null, "C001", "Camisa", 50000.0, 10);
        Producto guardado = productoService.crearProducto(producto);

        assertNotNull(guardado.getId());
        assertEquals("C001", guardado.getCodigo());
    }

    @Test
    @Order(2)
    void crearProducto_DeberiaFallarSiCodigoExiste() {
        productoService.crearProducto(new Producto(null, "C002", "Pantalón", 80000.0, 5));

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            productoService.crearProducto(new Producto(null, "C002", "Pantalón copia", 70000.0, 4));
        });

        assertEquals("Ya existe un producto con el mismo código", ex.getMessage());
    }

    // 2. Listar productos
    @Test
    @Order(3)
    void listarProductos_DeberiaRetornarLista() {
        productoService.crearProducto(new Producto(null, "L001", "Zapatos", 90000.0, 2));
        List<Producto> productos = productoService.listarProductos();

        assertEquals(1, productos.size());
    }

    @Test
    @Order(4)
    void listarProductos_DeberiaFallarSiListaVacia() {
        Exception ex = assertThrows(IllegalStateException.class, () -> {
            productoService.listarProductos();
        });

        assertEquals("No hay productos registrados en la base de datos", ex.getMessage());
    }

    // 3. Obtener por código
    @Test
    @Order(5)
    void obtenerPorCodigo_DeberiaRetornarProducto() {
        productoService.crearProducto(new Producto(null, "B001", "Blusa", 60000.0, 8));
        Producto encontrado = productoService.obtenerPorCodigo("B001");

        assertEquals("Blusa", encontrado.getNombre());
    }

    @Test
    @Order(6)
    void obtenerPorCodigo_DeberiaFallarSiNoExiste() {
        Exception ex = assertThrows(NoSuchElementException.class, () -> {
            productoService.obtenerPorCodigo("NOEXISTE");
        });

        assertEquals("No se encontró el producto con el código proporcionado", ex.getMessage());
    }

    // 4. Actualizar
    @Test
    @Order(7)
    void actualizarProducto_DeberiaActualizarCorrectamente() {
        productoService.crearProducto(new Producto(null, "A001", "Camisa", 50000.0, 10));

        Producto actualizado = new Producto(null, "A001", "Camisa actualizada", 60000.0, 15);
        Producto resultado = productoService.actualizarProducto("A001", actualizado);

        assertEquals("Camisa actualizada", resultado.getNombre());
        assertEquals(60000.0, resultado.getPrecio());
    }

    @Test
    @Order(8)
    void actualizarProducto_DeberiaFallarSiCodigoNoCoincide() {
        productoService.crearProducto(new Producto(null, "A002", "Camisa", 50000.0, 10));

        Producto conCodigoCambiado = new Producto(null, "OTRO", "Modificado", 70000.0, 5);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            productoService.actualizarProducto("A002", conCodigoCambiado);
        });

        assertEquals("No se puede modificar el código del producto", ex.getMessage());
    }

    @Test
    @Order(9)
    void actualizarProducto_DeberiaFallarSiNoExiste() {
        Producto producto = new Producto(null, "NOHAY", "Nada", 10000.0, 1);

        Exception ex = assertThrows(NoSuchElementException.class, () -> {
            productoService.actualizarProducto("NOHAY", producto);
        });

        assertEquals("No se encontró el producto con el código proporcionado", ex.getMessage());
    }

    // 5. Eliminar
    @Test
    @Order(10)
    void eliminarProducto_DeberiaEliminarCorrectamente() {
        productoService.crearProducto(new Producto(null, "E001", "Eliminarme", 9999.0, 1));
        productoService.eliminarProducto("E001");

        Exception ex = assertThrows(NoSuchElementException.class, () -> {
            productoService.obtenerPorCodigo("E001");
        });

        assertEquals("No se encontró el producto con el código proporcionado", ex.getMessage());
    }

    @Test
    @Order(11)
    void eliminarProducto_DeberiaFallarSiNoExiste() {
        Exception ex = assertThrows(NoSuchElementException.class, () -> {
            productoService.eliminarProducto("X999");
        });

        assertEquals("No se encontró el producto con el código proporcionado", ex.getMessage());
    }
}
