import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Collections;
import java.util.NoSuchElementException;

import com.eam.microservicio_spring_mariadb.repository.ProductoRepository;
import com.eam.microservicio_spring_mariadb.service.ProductoService;
import com.eam.microservicio_spring_mariadb.entity.Producto;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void crearProducto_DeberiaGuardarProductoCorrectamente() {
        Producto producto = new Producto(null, "P001", "Camisa", 50000.0, 10);
        when(productoRepository.existsByCodigo("P001")).thenReturn(false);
        when(productoRepository.save(any())).thenReturn(producto);

        Producto resultado = productoService.crearProducto(producto);

        assertNotNull(resultado);
        assertEquals("P001", resultado.getCodigo());
        verify(productoRepository).save(producto);
    }

    @Test
    void crearProducto_DeberiaLanzarExcepcionSiCodigoYaExiste() {
        Producto producto = new Producto(null, "P001", "Camisa", 50000.0, 10);
        when(productoRepository.existsByCodigo("P001")).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productoService.crearProducto(producto);
        });

        assertEquals("Ya existe un producto con el mismo código", exception.getMessage());
        verify(productoRepository, never()).save(any());
    }

    @Test
    void crearProducto_DeberiaLanzarExcepcionSiProductoEsNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productoService.crearProducto(null);
        });

        assertEquals("El producto no puede ser nulo", exception.getMessage());
    }

    @Test
    void listarProductos_DeberiaRetornarListaDeProductos() {
        List<Producto> lista = List.of(
            new Producto(1L, "P001", "Camisa", 50000.0, 10),
            new Producto(2L, "P002", "Pantalón", 80000.0, 5)
        );
        when(productoRepository.findAll()).thenReturn(lista);

        List<Producto> resultado = productoService.listarProductos();

        assertEquals(2, resultado.size());
        assertEquals("P001", resultado.get(0).getCodigo());
        verify(productoRepository).findAll();
    }

    @Test
    void listarProductos_DeberiaLanzarExcepcionSiNoHayProductos() {
        when(productoRepository.findAll()).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            productoService.listarProductos();
        });

        assertEquals("No hay productos registrados en la base de datos", exception.getMessage());
        verify(productoRepository).findAll();
    }

    @Test
    void obtenerProductoPorCodigo_DeberiaRetornarProductoSiExiste() {
        Producto producto = new Producto(1L, "P001", "Camisa", 50000.0, 10);
        when(productoRepository.findByCodigo("P001")).thenReturn(Optional.of(producto));

        Producto resultado = productoService.obtenerPorCodigo("P001");

        assertNotNull(resultado);
        assertEquals("Camisa", resultado.getNombre());
        assertEquals("P001", resultado.getCodigo());
        verify(productoRepository).findByCodigo("P001");
    }

    @Test
    void obtenerProductoPorCodigo_DeberiaLanzarExcepcionSiNoExiste() {
        when(productoRepository.findByCodigo("P999")).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            productoService.obtenerPorCodigo("P999");
        });

        assertEquals("No se encontró el producto con el código proporcionado", exception.getMessage());
        verify(productoRepository).findByCodigo("P999");
    }

    @Test
    void actualizarProducto_DeberiaActualizarCorrectamenteSiExisteYCodigoCoincide() {
        Producto existente = new Producto(1L, "P001", "Camisa", 50000.0, 10);
        Producto actualizado = new Producto(1L, "P001", "Camisa actualizada", 55000.0, 15);

        when(productoRepository.findByCodigo("P001")).thenReturn(Optional.of(existente));
        when(productoRepository.save(any())).thenReturn(actualizado);

        Producto resultado = productoService.actualizarProducto("P001", actualizado);

        assertNotNull(resultado);
        assertEquals("Camisa actualizada", resultado.getNombre());
        assertEquals(55000f, resultado.getPrecio());
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void actualizarProducto_DeberiaLanzarExcepcionSiCodigoNoCoincide() {
        Producto actualizado = new Producto(null, "P999", "Error", 1000.0, 1);  // código modificado

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productoService.actualizarProducto("P001", actualizado);
        });

        assertEquals("No se permite modificar el código del producto", exception.getMessage());
        verify(productoRepository, never()).save(any());

    }

    @Test
    void actualizarProducto_DeberiaLanzarExcepcionSiProductoNoExiste() {
        Producto actualizado = new Producto(null, "P001", "Camisa actualizada", 55000.0, 15);

        when(productoRepository.findByCodigo("P001")).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            productoService.actualizarProducto("P001", actualizado);
        });

        assertEquals("No se encontró el producto con el código proporcionado", exception.getMessage());
        verify(productoRepository, never()).save(any());
    }

    @Test
    void eliminarProductoPorCodigo_DeberiaEliminarProductoSiExiste() {
        Producto producto = new Producto(1L, "P001", "Camisa", 50000.0, 10);
        when(productoRepository.findByCodigo("P001")).thenReturn(Optional.of(producto));

        productoService.eliminarProducto("P001");

        verify(productoRepository).delete(producto);
    }

    @Test
    void eliminarProductoPorCodigo_DeberiaLanzarExcepcionSiNoExiste() {
        when(productoRepository.findByCodigo("P999")).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            productoService.eliminarProducto("P999");
        });

        assertEquals("No se encontró el producto con el código proporcionado", exception.getMessage());
        verify(productoRepository, never()).delete(any());
    }

}
