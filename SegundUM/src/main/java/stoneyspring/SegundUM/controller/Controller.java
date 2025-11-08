package stoneyspring.SegundUM.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stoneyspring.SegundUM.dominio.Categoria;
import stoneyspring.SegundUM.dominio.EstadoProducto;
import stoneyspring.SegundUM.dominio.Producto;
import stoneyspring.SegundUM.dominio.ResumenProducto;
import stoneyspring.SegundUM.servicio.FactoriaServicios;
import stoneyspring.SegundUM.servicio.ServicioException;
import stoneyspring.SegundUM.servicio.categorias.ServicioCategorias;
import stoneyspring.SegundUM.servicio.productos.ServicioProductos;
import stoneyspring.SegundUM.servicio.usuarios.ServicioUsuarios;

/**
 * Controlador principal de la aplicación.
 * <p>
 * Este controlador captura todas las excepciones de los servicios y las gestiona
 * sin interrumpir la ejecución del programa. Los errores se registran mediante
 * el logger y se retornan valores por defecto apropiados.
 * </p>
 * 
 * @see ServicioUsuarios
 * @see ServicioProductos
 * @see ServicioCategorias
 */
public class Controller {

    private static final Logger logger = LoggerFactory.getLogger(Controller.class);
    
    private ServicioUsuarios servicioUsuarios;
    private ServicioProductos servicioProductos;
    private ServicioCategorias servicioCategorias;

    public Controller() {
        this.servicioUsuarios = FactoriaServicios.getServicio(ServicioUsuarios.class);
        this.servicioProductos = FactoriaServicios.getServicio(ServicioProductos.class);
        this.servicioCategorias = FactoriaServicios.getServicio(ServicioCategorias.class);
    }

    // ========== CASOS DE USO DE USUARIO ==========

    /**
     * CU1: Registrar usuario en la aplicación
     * 
     * @param email Email del usuario (debe ser único)
     * @param nombre Nombre del usuario
     * @param apellidos Apellidos del usuario
     * @param clave Contraseña del usuario
     * @param fechaNacimiento Fecha de nacimiento
     * @param telefono Teléfono (opcional)
     * @return ID del usuario creado, o null si hay algún error
     */
    public String registrarUsuario(String email, String nombre, String apellidos, 
                                   String clave, LocalDate fechaNacimiento, 
                                   String telefono) {
        try {
            if (email == null || email.trim().isEmpty()) {
                logger.warn("Intento de registro con email vacío");
                return null;
            }
            if (nombre == null || nombre.trim().isEmpty()) {
                logger.warn("Intento de registro con nombre vacío");
                return null;
            }
            if (apellidos == null || apellidos.trim().isEmpty()) {
                logger.warn("Intento de registro con apellidos vacíos");
                return null;
            }
            if (clave == null || clave.trim().isEmpty()) {
                logger.warn("Intento de registro con clave vacía");
                return null;
            }
            if (fechaNacimiento == null) {
                logger.warn("Intento de registro con fecha de nacimiento nula");
                return null;
            }

            return servicioUsuarios.altaUsuario(email, nombre, apellidos, clave, fechaNacimiento, telefono);
        } catch (ServicioException e) {
            logger.error("Error al registrar el usuario con email: " + email, e);
            return null;
        }
    }

    /**
     * CU2: Modificar datos personales de usuario
     * 
     * @param usuarioId ID del usuario a modificar
     * @param nombre Nuevo nombre (null si no se modifica)
     * @param apellidos Nuevos apellidos (null si no se modifica)
     * @param clave Nueva contraseña (null si no se modifica)
     * @param fechaNacimiento Nueva fecha de nacimiento (null si no se modifica)
     * @param telefono Nuevo teléfono (null si no se modifica)
     * @return true si la modificación fue exitosa, false en caso contrario
     */
    public boolean modificarDatosPersonales(String usuarioId, String nombre, 
                                           String apellidos, String clave, 
                                           LocalDate fechaNacimiento, 
                                           String telefono) {
        try {
            if (usuarioId == null || usuarioId.trim().isEmpty()) {
                logger.warn("Intento de modificación de usuario con ID vacío");
                return false;
            }

            servicioUsuarios.modificarUsuario(usuarioId, nombre, apellidos, 
                                             clave, fechaNacimiento, telefono);
            return true;
        } catch (ServicioException e) {
            logger.error("Error al modificar datos del usuario con ID: " + usuarioId, e);
            return false;
        }
    }

    /**
     * CU3: Dar de alta un producto para la venta
     * 
     * @param titulo Título del producto
     * @param descripcion Descripción del producto
     * @param precio Precio del producto
     * @param estado Estado del producto
     * @param categoriaId ID de la categoría
     * @param envioDisponible Si está disponible el envío
     * @param vendedorId ID del usuario vendedor
     * @return ID del producto creado, o null si hay algún error
     */
    public String darAltaProducto(String titulo, String descripcion, 
                                 BigDecimal precio, EstadoProducto estado, 
                                 String categoriaId, boolean envioDisponible, 
                                 String vendedorId) {
        try {
            if (titulo == null || titulo.trim().isEmpty()) {
                logger.warn("Intento de alta de producto con título vacío");
                return null;
            }
            if (precio == null || precio.compareTo(BigDecimal.ZERO) <= 0) {
                logger.warn("Intento de alta de producto con precio inválido: " + precio);
                return null;
            }
            if (estado == null) {
                logger.warn("Intento de alta de producto sin estado");
                return null;
            }
            if (categoriaId == null || categoriaId.trim().isEmpty()) {
                logger.warn("Intento de alta de producto sin categoría");
                return null;
            }
            if (vendedorId == null || vendedorId.trim().isEmpty()) {
                logger.warn("Intento de alta de producto sin vendedor");
                return null;
            }

            return servicioProductos.altaProducto(titulo, descripcion, precio, estado, 
                                                 categoriaId, envioDisponible, vendedorId);
        } catch (ServicioException e) {
            logger.error("Error al dar de alta el producto: " + titulo, e);
            return null;
        }
    }

    /**
     * CU4: Modificar producto a la venta (precio y/o descripción)
     * 
     * @param productoId ID del producto a modificar
     * @param nuevoPrecio Nuevo precio (null si no se modifica)
     * @param nuevaDescripcion Nueva descripción (null si no se modifica)
     * @return true si la modificación fue exitosa, false en caso contrario
     */
    public boolean modificarProducto(String productoId, BigDecimal nuevoPrecio, 
                                    String nuevaDescripcion) {
        try {
            if (productoId == null || productoId.trim().isEmpty()) {
                logger.warn("Intento de modificación de producto con ID vacío");
                return false;
            }
            
            if (nuevoPrecio == null && nuevaDescripcion == null) {
                logger.warn("Intento de modificación de producto sin especificar cambios");
                return false;
            }
            
            if (nuevoPrecio != null && nuevoPrecio.compareTo(BigDecimal.ZERO) <= 0) {
                logger.warn("Intento de modificación de producto con precio inválido: " + nuevoPrecio);
                return false;
            }

            servicioProductos.modificarProducto(productoId, nuevoPrecio, nuevaDescripcion);
            return true;
        } catch (ServicioException e) {
            logger.error("Error al modificar el producto con ID: " + productoId, e);
            return false;
        }
    }

    /**
     * CU5: Asociar lugar de recogida a un producto
     * 
     * @param productoId ID del producto
     * @param descripcion Descripción del lugar de recogida
     * @param longitud Longitud geográfica
     * @param latitud Latitud geográfica
     * @return true si la asociación fue exitosa, false en caso contrario
     */
    public boolean asociarLugarRecogida(String productoId, String descripcion, 
                                       Double longitud, Double latitud) {
        try {
            if (productoId == null || productoId.trim().isEmpty()) {
                logger.warn("Intento de asociar lugar de recogida con ID de producto vacío");
                return false;
            }
            if (descripcion == null || descripcion.trim().isEmpty()) {
                logger.warn("Intento de asociar lugar de recogida con descripción vacía");
                return false;
            }
            if (longitud == null || latitud == null) {
                logger.warn("Intento de asociar lugar de recogida sin coordenadas completas");
                return false;
            }
            if (longitud < -180 || longitud > 180) {
                logger.warn("Intento de asociar lugar de recogida con longitud inválida: " + longitud);
                return false;
            }
            if (latitud < -90 || latitud > 90) {
                logger.warn("Intento de asociar lugar de recogida con latitud inválida: " + latitud);
                return false;
            }

            servicioProductos.asignarLugarRecogida(productoId, descripcion, longitud, latitud);
            return true;
        } catch (ServicioException e) {
            logger.error("Error al asociar lugar de recogida al producto con ID: " + productoId, e);
            return false;
        }
    }

    /**
     * CU6: Obtener resumen mensual de productos en venta y sus visualizaciones
     * 
     * @param mes Mes (1-12)
     * @param anio Año
     * @param emailVendedor Email del vendedor (opcional, puede ser null para obtener todos)
     * @return Lista de resúmenes de productos ordenados por visualizaciones (descendente),
     *         o lista vacía si hay algún error
     */
    public List<ResumenProducto> obtenerResumenMensual(int mes, int anio, String emailVendedor) {
        try {
            if (mes < 1 || mes > 12) {
                logger.warn("Intento de obtener resumen mensual con mes inválido: " + mes);
                return Collections.emptyList();
            }
            if (anio < 1900 || anio > 2100) {
                logger.warn("Intento de obtener resumen mensual con año inválido: " + anio);
                return Collections.emptyList();
            }

            return servicioProductos.historialMes(mes, anio, emailVendedor);
        } catch (ServicioException e) {
            logger.error("Error al obtener resumen mensual para " + mes + "/" + anio, e);
            return Collections.emptyList();
        }
    }

    /**
     * CU7: Consultar productos a la venta con filtros
     * 
     * @param categoriaId ID de la categoría (opcional, puede ser null)
     * @param textoBusqueda Texto a buscar en la descripción (opcional, puede ser null)
     * @param estadoMinimo Estado mínimo del producto (opcional, puede ser null)
     * @param precioMaximo Precio máximo (opcional, puede ser null)
     * @return Lista de productos que cumplen los criterios, o lista vacía si hay algún error
     */
    public List<Producto> buscarProductos(String categoriaId, String textoBusqueda, 
                                         EstadoProducto estadoMinimo, 
                                         BigDecimal precioMaximo) {
        try {
            if (precioMaximo != null && precioMaximo.compareTo(BigDecimal.ZERO) < 0) {
                logger.warn("Intento de búsqueda con precio máximo negativo: " + precioMaximo);
                return Collections.emptyList();
            }

            return servicioProductos.buscarProductos(categoriaId, textoBusqueda, 
                                                    estadoMinimo, precioMaximo);
        } catch (ServicioException e) {
            logger.error("Error al buscar productos", e);
            return Collections.emptyList();
        }
    }

    // ========== CASOS DE USO DE ADMINISTRADOR ==========

    /**
     * CU8: Cargar nuevas categorías desde archivo XML (administrador)
     * 
     * @param rutaArchivoXML Ruta del archivo XML con la jerarquía de categorías
     * @return true si la carga fue exitosa, false en caso contrario
     */
    public boolean cargarCategorias(String rutaArchivoXML) {
        try {
            if (rutaArchivoXML == null || rutaArchivoXML.trim().isEmpty()) {
                logger.error("La ruta del archivo XML es nula o vacía");
                return false;
            }

            servicioCategorias.cargarJerarquia(rutaArchivoXML);
            return true;
        } catch (ServicioException e) {
            logger.error("Error al cargar categorías desde XML: " + rutaArchivoXML, e);
            return false;
        }
    }

    /**
     * CU9: Modificar la descripción de una categoría existente (administrador)
     * 
     * @param categoriaId ID de la categoría
     * @param nuevaDescripcion Nueva descripción
     * @return true si la modificación fue exitosa, false en caso contrario
     */
    public boolean modificarDescripcionCategoria(String categoriaId, 
                                                String nuevaDescripcion) {
        try {
            if (categoriaId == null || categoriaId.trim().isEmpty()) {
                logger.warn("Intento de modificar categoría con ID vacío");
                return false;
            }
            if (nuevaDescripcion == null || nuevaDescripcion.trim().isEmpty()) {
                logger.warn("Intento de modificar categoría con descripción vacía");
                return false;
            }

            servicioCategorias.modificarDescripcion(categoriaId, nuevaDescripcion);
            return true;
        } catch (ServicioException e) {
            logger.error("Error al modificar descripción de categoría con ID: " + categoriaId, e);
            return false;
        }
    }

    // ========== MÉTODOS AUXILIARES Y DE CONSULTA ==========

    /**
     * Obtener todas las categorías raíz
     * 
     * @return Lista de categorías raíz, o lista vacía si hay algún error
     */
    public List<Categoria> obtenerCategoriasRaiz() {
        try {
            return servicioCategorias.getCategoriasRaiz();
        } catch (ServicioException e) {
            logger.error("Error al obtener categorías raíz", e);
            return Collections.emptyList();
        }
    }

    /**
     * Obtener descendientes de una categoría
     * 
     * @param categoriaId ID de la categoría padre
     * @return Lista de categorías descendientes, o lista vacía si hay algún error
     */
    public List<Categoria> obtenerDescendientesCategoria(String categoriaId) {
        try {
            if (categoriaId == null || categoriaId.trim().isEmpty()) {
                logger.warn("Intento de obtener descendientes con ID de categoría vacío");
                return Collections.emptyList();
            }

            return servicioCategorias.getDescendientes(categoriaId);
        } catch (ServicioException e) {
            logger.error("Error al obtener descendientes de categoría con ID: " + categoriaId, e);
            return Collections.emptyList();
        }
    }

    /**
     * Añadir visualización a un producto
     * 
     * @param productoId ID del producto
     * @return true si se añadió la visualización correctamente, false en caso contrario
     */
    public boolean registrarVisualizacionProducto(String productoId) {
        try {
            if (productoId == null || productoId.trim().isEmpty()) {
                logger.warn("Intento de registrar visualización con ID de producto vacío");
                return false;
            }

            servicioProductos.anadirVisualizacion(productoId);
            return true;
        } catch (ServicioException e) {
            logger.error("Error al registrar visualización del producto con ID: " + productoId, e);
            return false;
        }
    }
}