package stoneyspring.SegundUM.servicio.productos;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

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
            // Obtener categoría y vendedor (verificamos existencia)
            Categoria categoria = repositorioCategorias.getById(categoriaId);
            Usuario vendedor = repositorioUsuarios.getById(vendedorId);

            String id = UUID.randomUUID().toString();

            Producto p = new Producto(id, titulo, descripcion, precio, estado, categoria, envioDisponible, vendedor);

            return repositorioProductos.add(p);
        } catch (EntidadNoEncontrada e) {
            throw new ServicioException("Categoría o vendedor no encontrados", e);
        } catch (RepositorioException e) {
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
            throw new ServicioException("Producto no encontrado: " + productoId, e);
        } catch (RepositorioException e) {
            throw new ServicioException("Error al asignar lugar de recogida", e);
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
            throw new ServicioException("Producto no encontrado: " + productoId, e);
        } catch (RepositorioException e) {
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
            throw new ServicioException("Producto no encontrado: " + productoId, e);
        } catch (RepositorioException e) {
            throw new ServicioException("Error al añadir visualización", e);
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

    @Override
    public List<Producto> buscarProductos(String categoriaId, String texto, EstadoProducto estadoMinimo, BigDecimal precioMaximo) throws ServicioException {
        try {
            return repositorioProductos.buscarProductos(categoriaId, texto, estadoMinimo, precioMaximo);
        } catch (RepositorioException e) {
            throw new ServicioException("Error buscando productos", e);
        }
    }
}