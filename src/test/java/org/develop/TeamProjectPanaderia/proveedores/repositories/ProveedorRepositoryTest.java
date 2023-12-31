package org.develop.TeamProjectPanaderia.proveedores.repositories;

import org.develop.TeamProjectPanaderia.proveedores.models.Proveedor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(properties = "spring.sql.init.mode = never")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DataJpaTest
public class ProveedorRepositoryTest {
    @Autowired
    private ProveedorRepository proveedorRepository;
    @Autowired
    private TestEntityManager entityManager;

    private Proveedor proveedor1, proveedor2;

    @BeforeEach
    void setUp(){
        proveedor1 = Proveedor.builder()
                .id(1L)
                .nif("11")
                .numero("12")
                .nombre("Juan")
                .build();
        proveedor2 = Proveedor.builder()
                .id(2L)
                .nif("22")
                .numero("21")
                .nombre("Jose")
                .build();

        entityManager.merge(proveedor1);
        entityManager.merge(proveedor2);
        entityManager.flush();
    }

    @Test
    void findById() {
        // Obtener un proveedor por ID y verificar que los datos coincidan
        Optional<Proveedor> foundProveedor = proveedorRepository.findById(proveedor1.getId());
        assertTrue(foundProveedor.isPresent());
        assertEquals("11", foundProveedor.get().getNif());
        assertEquals("12", foundProveedor.get().getNumero());
        assertEquals("Juan", foundProveedor.get().getNombre());
    }

    @Test
    void findByNIF() {
        // Buscar por NIF y verificar que se encuentre el proveedor correcto
        Proveedor foundProveedor = proveedorRepository.findByNIF("22");
        assertNotNull(foundProveedor);
        assertEquals("22", foundProveedor.getNif());
        assertEquals("21", foundProveedor.getNumero());
        assertEquals("Jose", foundProveedor.getNombre());
    }

    @Test
    void findByNombre() {
        // Buscar por nombre y verificar que se encuentren los proveedores correctos
        List<Proveedor> foundProveedores = proveedorRepository.findByNombre("Juan");
        assertNotNull(foundProveedores);
        assertEquals(1, foundProveedores.size());
        assertEquals("11", foundProveedores.get(0).getNif());
        assertEquals("12", foundProveedores.get(0).getNumero());
        assertEquals("Juan", foundProveedores.get(0).getNombre());
    }

    @Test
    void findAll() {
        // Obtener todos los proveedores y verificar que la lista tenga el tamaño correcto
        List<Proveedor> allProveedores = proveedorRepository.findAll();
        assertNotNull(allProveedores);
        assertEquals(2, allProveedores.size());
    }

    @Test
    void deleteById() {
        // Borrar un proveedor por ID y verificar que ya no se encuentre en la base de datos
        proveedorRepository.deleteById(proveedor1.getId());
        entityManager.flush();

        Optional<Proveedor> deletedProveedor = proveedorRepository.findById(proveedor1.getId());
        assertFalse(deletedProveedor.isPresent());
    }
    @Test
    void save() {
        // Crear un nuevo proveedor y guardarlo en la base de datos
        Proveedor nuevoProveedor = Proveedor.builder()
                .nif("33")
                .numero("31")
                .nombre("Maria")
                .build();

        Proveedor savedProveedor = proveedorRepository.save(nuevoProveedor);
        entityManager.flush();

        // Obtener el proveedor por ID y verificar que los datos coincidan
        Optional<Proveedor> foundProveedor = proveedorRepository.findById(savedProveedor.getId());
        assertTrue(foundProveedor.isPresent());
        assertEquals("33", foundProveedor.get().getNif());
        assertEquals("31", foundProveedor.get().getNumero());
        assertEquals("Maria", foundProveedor.get().getNombre());
    }
}
