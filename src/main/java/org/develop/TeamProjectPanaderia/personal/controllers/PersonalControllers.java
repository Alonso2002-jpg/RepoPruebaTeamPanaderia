package org.develop.TeamProjectPanaderia.personal.controllers;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.develop.TeamProjectPanaderia.personal.dto.PersonalCreateDto;
import org.develop.TeamProjectPanaderia.personal.dto.PersonalResponseDto;
import org.develop.TeamProjectPanaderia.personal.dto.PersonalUpdateDto;
import org.develop.TeamProjectPanaderia.personal.mapper.PersonalMapper;
import org.develop.TeamProjectPanaderia.personal.models.Personal;
import org.develop.TeamProjectPanaderia.personal.services.PersonalService;
import org.develop.TeamProjectPanaderia.utils.pageresponse.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/personal")
public class PersonalControllers {
    private final PersonalService personalService;
    private final PersonalMapper personalMapper;

    @Autowired
    public PersonalControllers(PersonalService personalService, PersonalMapper personalMapper) {
        this.personalService = personalService;
        this.personalMapper = personalMapper;
    }

    @GetMapping
    public ResponseEntity<PageResponse<PersonalResponseDto>> getAllPersonal(
            @RequestParam(required = false) Optional<String> nombre,
            @RequestParam(required = false) Optional<String> dni,
            @RequestParam(required = false) Optional<String> categoria,
            @RequestParam(required = false) Optional<Boolean> isActivo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction){
        log.info("Obteniendo el Personal por : " + nombre + ", " + dni + ", " + categoria + ", " + isActivo);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(PageResponse.of(personalMapper.toPageResponse(personalService.findAll(nombre,dni,categoria, isActivo,pageable)),sortBy,direction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonalResponseDto> getPresonalfindById(@PathVariable String id){
        log.info("buscando por id");
        return ResponseEntity.ok(personalMapper.toResponseDto(personalService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<PersonalResponseDto> createPersonal(@Valid @RequestBody PersonalCreateDto personalCreateDto){
        log.info("create personal: " + personalCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(personalMapper.toResponseDto(personalService.save(personalCreateDto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonalResponseDto> updatePersonal(@PathVariable String id, @Valid @RequestBody PersonalUpdateDto personalUpdateteDto){
        log.info("update personal: " + id + " con personal: " + personalUpdateteDto);
        return ResponseEntity.ok(personalMapper.toResponseDto(personalService.update(id,personalUpdateteDto)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PersonalResponseDto> updatePartialPersonal(@PathVariable String id,@Valid @RequestBody PersonalUpdateDto personalUpdateteDto){
        log.info("actualizando partialmente personal: " + id + " con personal " + personalUpdateteDto);
        return ResponseEntity.ok(personalMapper.toResponseDto(personalService.update(id,personalUpdateteDto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Personal> deletePersonal(@PathVariable String id){
        log.info("Borrando persosonal por id: "+ id );
        personalService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

}
