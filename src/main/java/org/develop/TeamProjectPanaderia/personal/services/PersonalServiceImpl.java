package org.develop.TeamProjectPanaderia.personal.services;

import jakarta.persistence.criteria.Join;
import lombok.extern.slf4j.Slf4j;
import org.develop.TeamProjectPanaderia.categoria.models.Categoria;
import org.develop.TeamProjectPanaderia.categoria.services.CategoriaService;
import org.develop.TeamProjectPanaderia.personal.dto.PersonalCreateDto;
import org.develop.TeamProjectPanaderia.personal.dto.PersonalUpdateDto;
import org.develop.TeamProjectPanaderia.personal.exceptions.PersonalBadUuid;
import org.develop.TeamProjectPanaderia.personal.exceptions.PersonalNotSaved;
import org.develop.TeamProjectPanaderia.personal.exceptions.PersonalNotFoundException;
import org.develop.TeamProjectPanaderia.personal.mapper.PersonalMapper;
import org.develop.TeamProjectPanaderia.personal.models.Personal;
import org.develop.TeamProjectPanaderia.personal.repositories.PersonalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@CacheConfig(cacheNames = "personal")
public class PersonalServiceImpl implements PersonalService {
    private final PersonalRepository personalRepository;
    private final PersonalMapper personalMapper;
    private final CategoriaService categoriaService;

    @Autowired
    public PersonalServiceImpl(PersonalRepository personalRepository, PersonalMapper personalMapper, CategoriaService categoriaService) {
        this.personalRepository = personalRepository;
        this.personalMapper = personalMapper;
        this.categoriaService = categoriaService;
    }

    @Override
    public Page<Personal> findAll(Optional<String> nombre, Optional<String> dni, Optional<String> seccion, Optional<Boolean> isActivo, Pageable pageable) {
        Specification<Personal> specNombrePersonal = (root, query, criteriaBuilder) ->
                nombre.map(n -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + n.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Personal> specDniPersonal = (root, query, criteriaBuilder) ->
                dni.map(n -> criteriaBuilder.like(criteriaBuilder.lower(root.get("dni")), "%" + n.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Personal> specSeccionPersonal = (root, query, criteriaBuilder) ->
                seccion.map(c ->{
                    Join<Personal, Categoria> categoriaJoin = root.join("seccion");
                    return criteriaBuilder.like(criteriaBuilder.lower(categoriaJoin.get("nameCategory")), "%" + c.toLowerCase() + "%");
                }).orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Personal> specIsActivo = (root, query, criteriaBuilder) ->
                isActivo.map(a -> criteriaBuilder.equal(root.get("active"), a))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Personal> criterio = Specification.where(specNombrePersonal)
                .and(specDniPersonal)
                .and(specSeccionPersonal)
                .and(specIsActivo);
        return personalRepository.findAll(criterio, pageable);
    }

    @Override
    @Cacheable
    public Personal findById(String id) {
        log.info("Buscando por id");
        try {
            var uuid = UUID.fromString(id);
            return personalRepository.findById(uuid).orElseThrow(() -> new PersonalNotFoundException(uuid) {
            });
        } catch (IllegalArgumentException e) {
            throw new PersonalBadUuid(id);
        }
    }

    @Override
    @CachePut
    public Personal save(PersonalCreateDto personalCreateDto) {
      log.info("Guardando Personal");
      if(personalRepository.findByDniEqualsIgnoreCase(personalCreateDto.dni()).isPresent()) {
          throw new PersonalNotSaved(personalCreateDto.dni());
      }
      Categoria categoria = categoriaService.findByName(personalCreateDto.seccion());
      UUID id = UUID.randomUUID();
      return personalRepository.save(personalMapper.toPersonalCreate(id, categoria, personalCreateDto));
    }

    @Override
    @CachePut
    public Personal update(String id, PersonalUpdateDto personalDto) {
        log.info("Actualizando");
        var personalUpd = this.findById(id);
        Categoria categoria = null;
        if (personalDto.seccion() != null && !personalDto.seccion().isEmpty()) {
           categoria= categoriaService.findByName(personalDto.seccion());
        } else {
            categoria = personalUpd.getSeccion();
        }
        return personalRepository.save(personalMapper.toPersonalUpdate(personalDto, personalUpd, categoria));
    }

    @Override
    @Cacheable
    public Personal findPersonalByDni(String dni) {
        return personalRepository.findByDniEqualsIgnoreCase(dni).orElseThrow(() -> new PersonalNotFoundException(dni));
    }

    @Override
    @CacheEvict
    public void deleteById(String id) {
        log.info("Borrando por id");
        var personal = findById(id);
        personalRepository.delete(personal);
    }

    @Override
    public List<Personal> findByActiveIs(Boolean isActive) {
        return personalRepository.findByIsActive(isActive);
    }
}
