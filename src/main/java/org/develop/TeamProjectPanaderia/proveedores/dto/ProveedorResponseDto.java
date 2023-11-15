package org.develop.TeamProjectPanaderia.proveedores.dto;

import lombok.*;
import org.develop.TeamProjectPanaderia.categoria.models.Categoria;

import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProveedorResponseDto {
    private String nif;
    private Categoria tipo;
    private String numero;
    private String nombre;
    private LocalDate fechaCreacion;
    private LocalDate fechaUpdate;

}
