package stoneyspring.SegundUM.repositorio;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import stoneyspring.SegundUM.dominio.Categoria;
import stoneyspring.SegundUM.dominio.EstadoProducto;
import stoneyspring.SegundUM.dominio.LugarRecogida;
import stoneyspring.SegundUM.dominio.Producto;
import stoneyspring.SegundUM.dominio.Usuario;
import stoneyspring.SegundUM.repositorio.AdHoc.RepositorioProductos;
import stoneyspring.SegundUM.repositorio.AdHoc.RepositorioUsuarios;

class RepositorioTest {

    private RepositorioUsuarios repositorioUsuarios;
    private RepositorioCategorias repositorioCategorias;
    private RepositorioProductos repositorioProductos;

    @BeforeEach
    void setUp() {
        // Inicializar repositorios
        repositorioUsuarios = FactoriaRepositorios.getRepositorio(Usuario.class);
        repositorioCategorias = FactoriaRepositorios.getRepositorio(Categoria.class);
        repositorioProductos = FactoriaRepositorios.getRepositorio(Producto.class);
    }

    @AfterEach
    void tearDown() {
        // Limpiar después de cada test si es necesario
    }

    @Test
    void testGuardarYRecuperarUsuario() throws RepositorioException, EntidadNoEncontrada {
        // Crear un usuario de prueba
        Usuario usuario = new Usuario(
            "user-test-001",
            "test@segundum.com",
            "Juan",
            "Pérez García",
            "password123",
            LocalDate.of(1995, 5, 15),
            "666777888"
        );

        // Guardar el usuario
        String id = repositorioUsuarios.add(usuario);
        assertNotNull(id, "El ID del usuario no debe ser null");
        assertEquals("user-test-001", id, "El ID debe coincidir");

        // Recuperar el usuario
        Usuario usuarioRecuperado = repositorioUsuarios.getById(id);
        assertNotNull(usuarioRecuperado, "El usuario recuperado no debe ser null");
        assertEquals("test@segundum.com", usuarioRecuperado.getEmail());
        assertEquals("Juan", usuarioRecuperado.getNombre());
        assertEquals("Pérez García", usuarioRecuperado.getApellidos());
        assertFalse(usuarioRecuperado.isAdministrador(), "Por defecto no debe ser administrador");

        System.out.println("✓ Usuario guardado y recuperado correctamente");
        System.out.println("  - ID: " + usuarioRecuperado.getId());
        System.out.println("  - Email: " + usuarioRecuperado.getEmail());
        System.out.println("  - Nombre: " + usuarioRecuperado.getNombre() + " " + usuarioRecuperado.getApellidos());
    }

    @Test
    void testGuardarYRecuperarCategoria() throws RepositorioException, EntidadNoEncontrada {
        // Crear una categoría de prueba
        Categoria categoria = new Categoria("cat-test-001", "Electrónica");
        categoria.setDescripcion("Productos electrónicos y tecnología");

        // Crear subcategorías
        Categoria subcat1 = new Categoria("cat-test-002", "Móviles");
        subcat1.setDescripcion("Teléfonos móviles y smartphones");
        
        Categoria subcat2 = new Categoria("cat-test-003", "Tablets");
        subcat2.setDescripcion("Tabletas y dispositivos táctiles");

        categoria.addSubcategoria(subcat1);
        categoria.addSubcategoria(subcat2);

        // Guardar la categoría (en cascada guardará las subcategorías)
        String id = repositorioCategorias.add(categoria);
        assertNotNull(id, "El ID de la categoría no debe ser null");

        // Recuperar la categoría
        Categoria categoriaRecuperada = repositorioCategorias.getById(id);
        assertNotNull(categoriaRecuperada, "La categoría recuperada no debe ser null");
        assertEquals("Electrónica", categoriaRecuperada.getNombre());
        assertEquals(2, categoriaRecuperada.getSubcategorias().size(), "Debe tener 2 subcategorías");
        assertTrue(categoriaRecuperada.esRaiz(), "Debe ser una categoría raíz");

        System.out.println("✓ Categoría guardada y recuperada correctamente");
        System.out.println("  - ID: " + categoriaRecuperada.getId());
        System.out.println("  - Nombre: " + categoriaRecuperada.getNombre());
        System.out.println("  - Subcategorías: " + categoriaRecuperada.getSubcategorias().size());
    }

    @Test
    void testGuardarYRecuperarProducto() throws RepositorioException, EntidadNoEncontrada {
        // Primero crear un usuario vendedor
        Usuario vendedor = new Usuario(
            "user-test-002",
            "vendedor@segundum.com",
            "María",
            "López",
            "pass456",
            LocalDate.of(1990, 3, 20),
            "611222333"
        );
        repositorioUsuarios.add(vendedor);

        // Crear una categoría
        Categoria categoria = new Categoria("cat-test-004", "Libros");
        repositorioCategorias.add(categoria);

        // Crear un producto
        Producto producto = new Producto(
            "prod-test-001",
            "Cálculo I - Libro universitario",
            "Libro de cálculo diferencial e integral en muy buen estado",
            new BigDecimal("25.50"),
            EstadoProducto.BUEN_ESTADO,
            categoria,
            true,
            vendedor
        );

        // Añadir lugar de recogida
        LugarRecogida lugar = new LugarRecogida(
            "Campus Universitario - Espinardo",
            -1.166667,
            37.983333
        );
        producto.setRecogida(lugar);

        // Guardar el producto
        String id = repositorioProductos.add(producto);
        assertNotNull(id, "El ID del producto no debe ser null");

        // Recuperar el producto
        Producto productoRecuperado = repositorioProductos.getById(id);
        assertNotNull(productoRecuperado, "El producto recuperado no debe ser null");
        assertEquals("Cálculo I - Libro universitario", productoRecuperado.getTitulo());
        assertEquals(new BigDecimal("25.50"), productoRecuperado.getPrecio());
        assertEquals(EstadoProducto.BUEN_ESTADO, productoRecuperado.getEstado());
        assertEquals(0, productoRecuperado.getVisualizaciones(), "Visualizaciones iniciales debe ser 0");
        assertNotNull(productoRecuperado.getRecogida(), "Debe tener lugar de recogida");
        assertNotNull(productoRecuperado.getFechaPublicacion(), "Debe tener fecha de publicación");

        System.out.println("✓ Producto guardado y recuperado correctamente");
        System.out.println("  - ID: " + productoRecuperado.getId());
        System.out.println("  - Título: " + productoRecuperado.getTitulo());
        System.out.println("  - Precio: " + productoRecuperado.getPrecio() + "€");
        System.out.println("  - Estado: " + productoRecuperado.getEstado());
        System.out.println("  - Vendedor: " + productoRecuperado.getVendedor().getNombre());
        System.out.println("  - Categoría: " + productoRecuperado.getCategoria().getNombre());
    }

    @Test
    void testActualizarUsuario() throws RepositorioException, EntidadNoEncontrada {
        // Crear y guardar un usuario
        Usuario usuario = new Usuario(
            "user-test-003",
            "actualizar@test.com",
            "Pedro",
            "Martínez",
            "pass789",
            LocalDate.of(1992, 7, 10),
            "622333444"
        );
        repositorioUsuarios.add(usuario);

        // Modificar datos
        usuario.setNombre("Pedro Actualizado");
        usuario.setTelefono("699888777");

        // Actualizar
        repositorioUsuarios.update(usuario);

        // Recuperar y verificar
        Usuario usuarioActualizado = repositorioUsuarios.getById("user-test-003");
        assertEquals("Pedro Actualizado", usuarioActualizado.getNombre());
        assertEquals("699888777", usuarioActualizado.getTelefono());

        System.out.println("✓ Usuario actualizado correctamente");
        System.out.println("  - Nombre actualizado: " + usuarioActualizado.getNombre());
        System.out.println("  - Teléfono actualizado: " + usuarioActualizado.getTelefono());
    }

    @Test
    void testListarTodosLosUsuarios() throws RepositorioException {
        // Crear varios usuarios
        Usuario usuario1 = new Usuario(
            "user-test-004",
            "user1@test.com",
            "Ana",
            "García",
            "pass1",
            LocalDate.of(1993, 1, 1),
            null
        );
        Usuario usuario2 = new Usuario(
            "user-test-005",
            "user2@test.com",
            "Carlos",
            "Sánchez",
            "pass2",
            LocalDate.of(1994, 2, 2),
            null
        );

        repositorioUsuarios.add(usuario1);
        repositorioUsuarios.add(usuario2);

        // Listar todos
        List<Usuario> usuarios = repositorioUsuarios.getAll();
        assertNotNull(usuarios, "La lista no debe ser null");
        assertTrue(usuarios.size() >= 2, "Debe haber al menos 2 usuarios");

        System.out.println("✓ Listado de usuarios:");
        for (Usuario u : usuarios) {
            System.out.println("  - " + u.getNombre() + " " + u.getApellidos() + " (" + u.getEmail() + ")");
        }
    }

    @Test
    void testBuscarUsuarioPorEmail() throws RepositorioException, EntidadNoEncontrada {
        // Crear y guardar un usuario
        Usuario usuario = new Usuario(
            "user-test-006",
            "buscar@test.com",
            "Laura",
            "Fernández",
            "pass999",
            LocalDate.of(1991, 8, 25),
            "633444555"
        );
        repositorioUsuarios.add(usuario);

        // Buscar por email
        Usuario usuarioEncontrado = repositorioUsuarios.getByEmail("buscar@test.com");
        assertNotNull(usuarioEncontrado, "Debe encontrar el usuario");
        assertEquals("Laura", usuarioEncontrado.getNombre());
        assertEquals("Fernández", usuarioEncontrado.getApellidos());

        System.out.println("✓ Usuario encontrado por email:");
        System.out.println("  - Email buscado: buscar@test.com");
        System.out.println("  - Usuario: " + usuarioEncontrado.getNombre() + " " + usuarioEncontrado.getApellidos());
    }

    @Test
    void testIncrementarVisualizacionesProducto() throws RepositorioException, EntidadNoEncontrada {
        // Crear dependencias
        Usuario vendedor = new Usuario(
            "user-test-007",
            "vendedor2@test.com",
            "Jorge",
            "Ruiz",
            "pass111",
            LocalDate.of(1988, 12, 5),
            null
        );
        repositorioUsuarios.add(vendedor);

        Categoria categoria = new Categoria("cat-test-005", "Deportes");
        repositorioCategorias.add(categoria);

        // Crear producto
        Producto producto = new Producto(
            "prod-test-002",
            "Bicicleta de montaña",
            "Bicicleta en excelente estado, poco uso",
            new BigDecimal("350.00"),
            EstadoProducto.COMO_NUEVO,
            categoria,
            false,
            vendedor
        );
        repositorioProductos.add(producto);

        // Incrementar visualizaciones
        producto.incrementarVisualizaciones();
        producto.incrementarVisualizaciones();
        producto.incrementarVisualizaciones();
        repositorioProductos.update(producto);

        // Verificar
        Producto productoActualizado = repositorioProductos.getById("prod-test-002");
        assertEquals(3, productoActualizado.getVisualizaciones(), "Debe tener 3 visualizaciones");

        System.out.println("✓ Visualizaciones incrementadas correctamente");
        System.out.println("  - Producto: " + productoActualizado.getTitulo());
        System.out.println("  - Visualizaciones: " + productoActualizado.getVisualizaciones());
    }

    @Test
    void testGetCategoriasRaiz() throws RepositorioException {
        // Crear categorías raíz
        Categoria cat1 = new Categoria("cat-test-006", "Moda");
        Categoria cat2 = new Categoria("cat-test-007", "Hogar");
        
        repositorioCategorias.add(cat1);
        repositorioCategorias.add(cat2);

        // Obtener categorías raíz
        List<Categoria> raices = repositorioCategorias.getCategoriasRaiz();
        assertNotNull(raices, "La lista no debe ser null");
        assertTrue(raices.size() >= 2, "Debe haber al menos 2 categorías raíz");

        System.out.println("✓ Categorías raíz encontradas:");
        for (Categoria c : raices) {
            System.out.println("  - " + c.getNombre() + " (ID: " + c.getId() + ")");
        }
    }

    @Test
    void testExisteEmail() throws RepositorioException {
        // Crear usuario
        Usuario usuario = new Usuario(
            "user-test-008",
            "existe@test.com",
            "Marta",
            "Jiménez",
            "pass222",
            LocalDate.of(1996, 4, 18),
            null
        );
        repositorioUsuarios.add(usuario);

        // Verificar existencia
        assertTrue(repositorioUsuarios.existeEmail("existe@test.com"), "El email debe existir");
        assertFalse(repositorioUsuarios.existeEmail("noexiste@test.com"), "El email no debe existir");

        System.out.println("✓ Verificación de email existente correcta");
        System.out.println("  - existe@test.com: SÍ existe");
        System.out.println("  - noexiste@test.com: NO existe");
    }
}