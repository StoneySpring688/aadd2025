package stoneyspring.SegundUM.servicio.productos;

import java.math.BigDecimal;
import java.util.List;

import stoneyspring.SegundUM.dominio.EstadoProducto;
import stoneyspring.SegundUM.dominio.Producto;
import stoneyspring.SegundUM.dominio.ResumenProducto;
import stoneyspring.SegundUM.servicio.ServicioException;

/**
 * Operaciones de negocio sobre productos.
 */
public interface ServicioProductos {

    /**
     * Alta de producto. Devuelve id generado.
     */
    String altaProducto(String titulo, String descripcion, BigDecimal precio,
                        EstadoProducto estado, String categoriaId, boolean envioDisponible,
                        String vendedorId) throws ServicioException;

    /**
     * Asigna lugar de recogida al producto.
     */
    void asignarLugarRecogida(String productoId, String descripcion, Double longitud, Double latitud) throws ServicioException;

    /**
     * Modifica precio y/o descripción del producto. Parámetros nulos no se modifican.
     */
    void modificarProducto(String productoId, BigDecimal nuevoPrecio, String nuevaDescripcion) throws ServicioException;

    /**
     * Incrementa en 1 el contador de visualizaciones.
     */
    void anadirVisualizacion(String productoId) throws ServicioException;

    /**
     * Historial del mes de un vendedor: devuelve resumen ordenado por visualizaciones (desc).
     */
    List<ResumenProducto> historialMesVendedor(int mes, int anio, String emailVendedor) throws ServicioException;
    
    /**
     * Historial del mes de: devuelve resumen ordenado por visualizaciones (desc).
     */
    List<ResumenProducto> historialMes(int mes, int anio) throws ServicioException;

    /**
     * Buscar productos con los criterios opcionales.
     */
    List<Producto> buscarProductos(String categoriaId, String texto, EstadoProducto estadoMinimo, BigDecimal precioMaximo) throws ServicioException;
}