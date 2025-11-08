package stoneyspring.SegundUM.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
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

public class Controller {

	Logger logger = LoggerFactory.getILoggerFactory().getLogger(Controller.class.getName());
	
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
     * @return ID del usuario creado
     * @throws ServicioException Si hay algún error en el registro
     */
    public String registrarUsuario(String email, String nombre, String apellidos, 
                                   String clave, LocalDate fechaNacimiento, 
                                   String telefono) throws ServicioException {
        
        if (email == null || email.trim().isEmpty()) {
            throw new ServicioException("El email es obligatorio");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ServicioException("El nombre es obligatorio");
        }
        if (apellidos == null || apellidos.trim().isEmpty()) {
            throw new ServicioException("Los apellidos son obligatorios");
        }
        if (clave == null || clave.trim().isEmpty()) {
            throw new ServicioException("La clave es obligatoria");
        }
        if (fechaNacimiento == null) {
            throw new ServicioException("La fecha de nacimiento es obligatoria");
        }

        return servicioUsuarios.altaUsuario(email, nombre, apellidos, clave, 
                                           fechaNacimiento, telefono);
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
     * @throws ServicioException Si hay algún error en la modificación
     */
    public void modificarDatosPersonales(String usuarioId, String nombre, 
                                        String apellidos, String clave, 
                                        LocalDate fechaNacimiento, 
                                        String telefono) throws ServicioException {
        
        if (usuarioId == null || usuarioId.trim().isEmpty()) {
            throw new ServicioException("El ID de usuario es obligatorio");
        }

        servicioUsuarios.modificarUsuario(usuarioId, nombre, apellidos, 
                                         clave, fechaNacimiento, telefono);
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
     * @return ID del producto creado
     * @throws ServicioException Si hay algún error en el alta
     */
    public String darAltaProducto(String titulo, String descripcion, 
                                 BigDecimal precio, EstadoProducto estado, 
                                 String categoriaId, boolean envioDisponible, 
                                 String vendedorId) throws ServicioException {
        
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new ServicioException("El título del producto es obligatorio");
        }
        if (precio == null || precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServicioException("El precio debe ser mayor que cero");
        }
        if (estado == null) {
            throw new ServicioException("El estado del producto es obligatorio");
        }
        if (categoriaId == null || categoriaId.trim().isEmpty()) {
            throw new ServicioException("La categoría es obligatoria");
        }
        if (vendedorId == null || vendedorId.trim().isEmpty()) {
            throw new ServicioException("El vendedor es obligatorio");
        }

        return servicioProductos.altaProducto(titulo, descripcion, precio, estado, 
                                             categoriaId, envioDisponible, vendedorId);
    }

    /**
     * CU4: Modificar producto a la venta (precio y/o descripción)
     * 
     * @param productoId ID del producto a modificar
     * @param nuevoPrecio Nuevo precio (null si no se modifica)
     * @param nuevaDescripcion Nueva descripción (null si no se modifica)
     * @throws ServicioException Si hay algún error en la modificación
     */
    public void modificarProducto(String productoId, BigDecimal nuevoPrecio, 
                                 String nuevaDescripcion) throws ServicioException {
        
        if (productoId == null || productoId.trim().isEmpty()) {
            throw new ServicioException("El ID del producto es obligatorio");
        }
        
        if (nuevoPrecio == null && nuevaDescripcion == null) {
            throw new ServicioException("Debe especificar al menos un campo a modificar");
        }
        
        if (nuevoPrecio != null && nuevoPrecio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServicioException("El precio debe ser mayor que cero");
        }

        servicioProductos.modificarProducto(productoId, nuevoPrecio, nuevaDescripcion);
    }

    /**
     * CU5: Asociar lugar de recogida a un producto
     * 
     * @param productoId ID del producto
     * @param descripcion Descripción del lugar de recogida
     * @param longitud Longitud geográfica
     * @param latitud Latitud geográfica
     * @throws ServicioException Si hay algún error al asociar el lugar
     */
    public void asociarLugarRecogida(String productoId, String descripcion, 
                                    Double longitud, Double latitud) throws ServicioException {
        
        if (productoId == null || productoId.trim().isEmpty()) {
            throw new ServicioException("El ID del producto es obligatorio");
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new ServicioException("La descripción del lugar es obligatoria");
        }
        if (longitud == null || latitud == null) {
            throw new ServicioException("Las coordenadas (longitud y latitud) son obligatorias");
        }
        if (longitud < -180 || longitud > 180) {
            throw new ServicioException("La longitud debe estar entre -180 y 180");
        }
        if (latitud < -90 || latitud > 90) {
            throw new ServicioException("La latitud debe estar entre -90 y 90");
        }

        servicioProductos.asignarLugarRecogida(productoId, descripcion, 
                                              longitud, latitud);
    }

    /**
     * CU6: Obtener resumen mensual de productos en venta y sus visualizaciones
     * 
     * @param mes Mes (1-12)
     * @param anio Año
     * @return Lista de resúmenes de productos ordenados por visualizaciones (descendente)
     * @throws ServicioException Si hay algún error al obtener el resumen
     */
    public List<ResumenProducto> obtenerResumenMensual(int mes, int anio) throws ServicioException {
        
        if (mes < 1 || mes > 12) {
            throw new ServicioException("El mes debe estar entre 1 y 12");
        }
        if (anio < 1900 || anio > 2100) {
            throw new ServicioException("El año no es válido");
        }

        return servicioProductos.historialMes(mes, anio);
    }

    /**
     * CU7: Consultar productos a la venta con filtros
     * 
     * @param categoriaId ID de la categoría (opcional, puede ser null)
     * @param textoBusqueda Texto a buscar en la descripción (opcional, puede ser null)
     * @param estadoMinimo Estado mínimo del producto (opcional, puede ser null)
     * @param precioMaximo Precio máximo (opcional, puede ser null)
     * @return Lista de productos que cumplen los criterios
     * @throws ServicioException Si hay algún error en la búsqueda
     */
    public List<Producto> buscarProductos(String categoriaId, String textoBusqueda, 
                                         EstadoProducto estadoMinimo, 
                                         BigDecimal precioMaximo) throws ServicioException {
        
        if (precioMaximo != null && precioMaximo.compareTo(BigDecimal.ZERO) < 0) {
            throw new ServicioException("El precio máximo no puede ser negativo");
        }

        return servicioProductos.buscarProductos(categoriaId, textoBusqueda, 
                                                estadoMinimo, precioMaximo);
    }

    // ========== CASOS DE USO DE ADMINISTRADOR ==========

    /**
     * CU8: Cargar nuevas categorías desde archivo XML (administrador)
     * 
     * @param rutaArchivoXML Ruta del archivo XML con la jerarquía de categorías
     * @throws ServicioException Si hay algún error al cargar las categorías
     */
    public void cargarCategorias(String rutaArchivoXML) throws ServicioException {
        
        if (rutaArchivoXML == null || rutaArchivoXML.trim().isEmpty()) {
        	logger.error("La ruta del archivo XML es nula o vacía");
            throw new ServicioException("La ruta del archivo XML es obligatoria");
        }

        servicioCategorias.cargarJerarquia(rutaArchivoXML);
    }

    /**
     * CU9: Modificar la descripción de una categoría existente (administrador)
     * 
     * @param categoriaId ID de la categoría
     * @param nuevaDescripcion Nueva descripción
     * @throws ServicioException Si hay algún error al modificar la categoría
     */
    public void modificarDescripcionCategoria(String categoriaId, 
                                             String nuevaDescripcion) throws ServicioException {
        
        if (categoriaId == null || categoriaId.trim().isEmpty()) {
            throw new ServicioException("El ID de la categoría es obligatorio");
        }
        if (nuevaDescripcion == null || nuevaDescripcion.trim().isEmpty()) {
            throw new ServicioException("La nueva descripción es obligatoria");
        }

        servicioCategorias.modificarDescripcion(categoriaId, nuevaDescripcion);
    }

    // ========== MÉTODOS AUXILIARES Y DE CONSULTA ==========

    /**
     * Obtener todas las categorías raíz
     * 
     * @return Lista de categorías raíz
     * @throws ServicioException Si hay algún error
     */
    public List<Categoria> obtenerCategoriasRaiz() throws ServicioException {
        return servicioCategorias.getCategoriasRaiz();
    }

    /**
     * Obtener descendientes de una categoría
     * 
     * @param categoriaId ID de la categoría padre
     * @return Lista de categorías descendientes
     * @throws ServicioException Si hay algún error
     */
    public List<Categoria> obtenerDescendientesCategoria(String categoriaId) throws ServicioException {
        
        if (categoriaId == null || categoriaId.trim().isEmpty()) {
            throw new ServicioException("El ID de la categoría es obligatorio");
        }

        return servicioCategorias.getDescendientes(categoriaId);
    }

    /**
     * Añadir visualización a un producto
     * 
     * @param productoId ID del producto
     * @throws ServicioException Si hay algún error
     */
    public void registrarVisualizacionProducto(String productoId) throws ServicioException {
        
        if (productoId == null || productoId.trim().isEmpty()) {
            throw new ServicioException("El ID del producto es obligatorio");
        }

        servicioProductos.anadirVisualizacion(productoId);
    }
}