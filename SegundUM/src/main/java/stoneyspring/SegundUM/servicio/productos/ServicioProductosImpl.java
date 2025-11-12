package stoneyspring.SegundUM.servicio.productos;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stoneyspring.SegundUM.servicio.ServicioException;
import stoneyspring.SegundUM.dominio.Categoria;
import stoneyspring.SegundUM.dominio.EstadoProducto;
import stoneyspring.SegundUM.dominio.LugarRecogida;
import stoneyspring.SegundUM.dominio.Producto;
import stoneyspring.SegundUM.dominio.Usuario;
import stoneyspring.SegundUM.dominio.ResumenProducto;
import stoneyspring.SegundUM.repositorio.FactoriaRepositorios;
import stoneyspring.SegundUM.repositorio.productos.RepositorioProductos;
import stoneyspring.SegundUM.repositorio.categorias.RepositorioCategorias;
import stoneyspring.SegundUM.repositorio.usuarios.RepositorioUsuarios;
import stoneyspring.SegundUM.repositorio.RepositorioException;
import stoneyspring.SegundUM.repositorio.EntidadNoEncontrada;

/**
 * Implementación del servicio de productos.
 */
public class ServicioProductosImpl implements ServicioProductos {

	private final Logger logger = LoggerFactory.getLogger(ServicioProductosImpl.class);
	
    private final RepositorioProductos repositorioProductos;
    private final RepositorioCategorias repositorioCategorias;
    private final RepositorioUsuarios repositorioUsuarios;

    public ServicioProductosImpl() {
        this.repositorioProductos = FactoriaRepositorios.getRepositorio(Producto.class);
        this.repositorioCategorias = FactoriaRepositorios.getRepositorio(Categoria.class);
        this.repositorioUsuarios = FactoriaRepositorios.getRepositorio(Usuario.class);
    }

    @Override
    public String altaProducto(String titulo, String descripcion, BigDecimal precio, EstadoProducto estado,
                               String categoriaId, boolean envioDisponible, String vendedorId) throws ServicioException {
        try {
            // VERIFICACIÓN: Obtener categoría y verificar que existe
            Categoria categoria;
            try {
            	logger.info("Obteniendo categoría con ID: " + categoriaId);
                categoria = repositorioCategorias.getById(categoriaId);
            } catch (EntidadNoEncontrada e) {
            	logger.error("Categoría con ID " + categoriaId + " no encontrada", e);
                throw new ServicioException("La categoría con ID " + categoriaId + " no existe en el sistema", e);
            }

            // VERIFICACIÓN: Obtener vendedor y verificar que existe
            Usuario vendedor;
            try {
            	logger.info("Obteniendo vendedor con ID: " + vendedorId);
                vendedor = repositorioUsuarios.getById(vendedorId);
            } catch (EntidadNoEncontrada e) {
            	logger.error("Vendedor con ID " + vendedorId + " no encontrado", e);
                throw new ServicioException("El vendedor con ID " + vendedorId + " no existe en el sistema", e);
            }

            String id = UUID.randomUUID().toString();

            Producto p = new Producto(id, titulo, descripcion, precio, estado, categoria, envioDisponible, vendedor);

            return repositorioProductos.add(p);
        } catch (RepositorioException e) {
        	logger.error("Error al dar de alta el producto", e);
            throw new ServicioException("Error al dar de alta el producto", e);
        }
    }

    @Override
    public void asignarLugarRecogida(String productoId, String descripcion, Double longitud, Double latitud) throws ServicioException {
        try {
            Producto p = repositorioProductos.getById(productoId);
            LugarRecogida lugar = new LugarRecogida(descripcion, longitud, latitud);
            p.setRecogida(lugar);
            repositorioProductos.update(p);
        } catch (EntidadNoEncontrada e) {
            // VERIFICACIÓN: El producto no existe
        	logger.error("Producto con ID " + productoId + " no encontrado", e);
            throw new ServicioException("El producto con ID " + productoId + " no existe en el sistema", e);
        } catch (RepositorioException e) {
        	logger.error("Error al asignar lugar de recogida al producto " + productoId, e);
            throw new ServicioException("Error al asignar lugar de recogida al producto " + productoId, e);
        }
    }

    @Override
    public void modificarProducto(String productoId, BigDecimal nuevoPrecio, String nuevaDescripcion) throws ServicioException {
        try {
            Producto p = repositorioProductos.getById(productoId);
            if (nuevoPrecio != null) p.setPrecio(nuevoPrecio);
            if (nuevaDescripcion != null) p.setDescripcion(nuevaDescripcion);
            repositorioProductos.update(p);
        } catch (EntidadNoEncontrada e) {
            // VERIFICACIÓN: el producto no existe
        	logger.error("Producto con ID " + productoId + " no encontrado", e);
            throw new ServicioException("El producto con ID " + productoId + " no existe en el sistema", e);
        } catch (RepositorioException e) {
        	logger.error("Error al modificar el producto " + productoId, e);
            throw new ServicioException("Error al modificar producto " + productoId, e);
        }
    }

    @Override
    public void anadirVisualizacion(String productoId) throws ServicioException {
        try {
            Producto p = repositorioProductos.getById(productoId);
            p.incrementarVisualizaciones();
            repositorioProductos.update(p);
        } catch (EntidadNoEncontrada e) {
            // VERIFICACIÓN: el producto no existe
        	logger.error("Producto con ID " + productoId + " no encontrado", e);
            throw new ServicioException("El producto con ID " + productoId + " no existe en el sistema", e);
        } catch (RepositorioException e) {
        	logger.error("Error al añadir visualización al producto " + productoId, e);
            throw new ServicioException("Error al añadir visualización al producto " + productoId, e);
        }
    }

    @Override
    public List<ResumenProducto> historialMesVendedor(int mes, int anio, String emailVendedor) throws ServicioException {
        try {
            return repositorioProductos.getHistorialMes(mes, anio, emailVendedor);
        } catch (RepositorioException e) {
            throw new ServicioException("Error al obtener historial del mes", e);
        }
    }

    @Override
    public List<Producto> buscarProductos(String categoriaId, String texto, EstadoProducto estadoMinimo, BigDecimal precioMaximo) throws ServicioException {
        try {
        	logger.info("Buscando productos con filtros - Categoría ID: " + categoriaId + ", Texto: " + texto + ", Estado mínimo: " + estadoMinimo + ", Precio máximo: " + precioMaximo);
            return repositorioProductos.buscarProductos(categoriaId, texto, estadoMinimo, precioMaximo);
        } catch (RepositorioException e) {
        	logger.error("Error buscando productos con los filtros proporcionados", e);
            throw new ServicioException("Error buscando productos", e);
        }
    }

	@Override
	public List<ResumenProducto> historialMes(int mes, int anio) throws ServicioException {
		try {
            return repositorioProductos.getHistorialMes(mes, anio);
        } catch (RepositorioException e) {
            throw new ServicioException("Error al obtener historial del mes", e);
        }
	}
}