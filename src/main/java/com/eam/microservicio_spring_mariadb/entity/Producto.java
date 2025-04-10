package com.eam.microservicio_spring_mariadb.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "productos", uniqueConstraints = {
        @UniqueConstraint(columnNames = "codigo")
})
@Getter
@Setter
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El código no puede estar vacío")
    @Size(min = 3, max = 20, message = "El código debe tener entre 3 y 20 caracteres")
    @Column(nullable = false, unique = true)
    private String codigo;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    @Column(nullable = false)
    private String nombre;

    @NotNull(message = "El precio no puede ser nulo")
    @Positive(message = "El precio debe ser mayor que cero")
    @Column(nullable = false)
    private Double precio;

    @NotNull(message = "La cantidad no puede ser nula")
    @Positive(message = "La cantidad debe ser mayor que cero")
    @Column(nullable = false)
    private Integer cantidad;

    // 🔽 Constructor vacío (ya lo agrega Lombok, pero puedes incluirlo explícitamente si quieres)
    public Producto() {
    }

    // 🔽 Constructor con todos los campos (sin el ID, que es autogenerado)
    public Producto(String codigo, String nombre, Double precio, Integer cantidad) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    // 🔽 Si necesitas también uno con ID (opcional para tests)
    public Producto(Long id, String codigo, String nombre, Double precio, Integer cantidad) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
    }
}
