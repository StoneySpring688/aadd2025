package stoneyspring.SegundUM.repositorio.productos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import stoneyspring.SegundUM.dominio.EstadoProducto;
import stoneyspring.SegundUM.dominio.Producto;
import stoneyspring.SegundUM.dominio.ResumenProducto;
import stoneyspring.SegundUM.repositorio.RepositorioException;
import stoneyspring.SegundUM.repositorio.RepositorioString;

/**
 * Repositorio específico para Productos con operaciones AdHoc.
 */
public interface RepositorioProductos extends RepositorioString<Producto> {
    
    /**
     * Obtiene los productos de un vendedor.
     */
    List<Producto> getProductosPorVendedor(String vendedorId) throws RepositorioException;
    
    /**
     * Busca productos por categoría, descripción, estado y precio máximo.
     * Todos los parámetros son opcionales (pueden ser null).
     */
    List<Producto> buscarProductos(
        String categoriaId,
        String textoBusqueda,
        EstadoProducto estadoMinimo,
        BigDecimal precioMaximo
    ) throws RepositorioException;
    
    /**
     * Obtiene el historial del mes ordenado por visualizaciones.
     */
    List<ResumenProducto> getHistorialMes(int mes, int anio, String emailVendedor) throws RepositorioException;
    
    /**
     * Obtiene productos publicados en un rango de fechas.
     */
    List<Producto> getProductosPorFechas(LocalDateTime inicio, LocalDateTime fin) throws RepositorioException;
}