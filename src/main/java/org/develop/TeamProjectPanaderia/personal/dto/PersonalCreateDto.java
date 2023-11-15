package org.develop.TeamProjectPanaderia.personal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import org.hibernate.validator.constraints.Length;


@Builder
public record PersonalCreateDto(
        @NotBlank(message = "El dni no puede estar vacio")  // ------------> Añadido (el dni SI O SI tiene que pasarlo)
        @Pattern(regexp = "^[0-9]{8}[a-zA-Z]$", message = "El DNI debe tener 8 numeros seguidos de una letra")   // ------------> Añadido (el dni debe tener el formato)
        String dni,
        @Length(min = 3, message = "El nombre debe contener al menos 3 letras") // ------------> Añadido (Aseguramos que el nombre tenga 3 letras al menos )
        @NotBlank(message = "El nombre no puede estar vacio")  // ------------> Añadido (el nombre SI O SI tiene que pasarlo)
        String nombre,
        @NotBlank(message = "La seccion no puede estar vacia") // ------------> Añadido (La seccion no puede estar vacia)
        String seccion,
        Boolean isActive
) {
}
// QUITAMOS LA FECHA DE BAJA, EL USUARIO NO NOS LA PASARA
// QUITAMOS LA FECHA DE ALTA, EL USUARIO NO NOS LA PASARA


