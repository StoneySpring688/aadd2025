package stoneyspring.SegundUM.repositorio.productos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import stoneyspring.SegundUM.dominio.Categoria;
import stoneyspring.SegundUM.dominio.EstadoProducto;
import stoneyspring.SegundUM.dominio.Producto;
import stoneyspring.SegundUM.dominio.ResumenProducto;
import stoneyspring.SegundUM.repositorio.RepositorioException;
import stoneyspring.SegundUM.repositorio.RepositorioJPA;
import stoneyspring.SegundUM.utils.EntityManagerHelper;

/**
 * Implementación JPA del repositorio de productos.
 */
public class RepositorioProductosJPA extends RepositorioJPA<Producto> implements RepositorioProductos {
    
    @Override
    public Class<Producto> getClase() {
        return Producto.class;
    }
    
    @Override
    public List<Producto> getProductosPorVendedor(String vendedorId) throws RepositorioException {
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            TypedQuery<Producto> query = em.createQuery(
                "SELECT p FROM Producto p WHERE p.vendedor.id = :vendedorId", 
                Producto.class
            );
            query.setParameter("vendedorId", vendedorId);
            
            return query.getResultList();
        } catch (Exception e) {
            throw new RepositorioException("Error al recuperar productos del vendedor " + vendedorId, e);
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
    }
    
    @Override
    public List<Producto> buscarProductos(
        String categoriaId,
        String textoBusqueda,
        EstadoProducto estadoMinimo,
        BigDecimal precioMaximo
    ) throws RepositorioException {
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            // Construir la consulta dinámicamente
            StringBuilder jpql = new StringBuilder("SELECT p FROM Producto p WHERE 1=1");
            
            // Lista de categorías a buscar (incluye descendientes)
            List<String> categoriasIds = new ArrayList<>();
            if (categoriaId != null) {
                Categoria categoria = em.find(Categoria.class, categoriaId);
                if (categoria != null) {
                    categoriasIds.add(categoriaId);
                    categoria.obtenerDescendientes().forEach(c -> categoriasIds.add(c.getId()));
                }
            }
            
            if (!categoriasIds.isEmpty()) {
                jpql.append(" AND p.categoria.id IN :categoriasIds");
            }
            
            if (textoBusqueda != null && !textoBusqueda.trim().isEmpty()) {
                jpql.append(" AND LOWER(p.descripcion) LIKE LOWER(:texto)");
            }
            
            if (estadoMinimo != null) {
                jpql.append(" AND p.estado IN :estados");
            }
            
            if (precioMaximo != null) {
                jpql.append(" AND p.precio <= :precioMaximo");
            }
            
            TypedQuery<Producto> query = em.createQuery(jpql.toString(), Producto.class);
            
            // Establecer parámetros
            if (!categoriasIds.isEmpty()) {
                query.setParameter("categoriasIds", categoriasIds);
            }
            
            if (textoBusqueda != null && !textoBusqueda.trim().isEmpty()) {
                query.setParameter("texto", "%" + textoBusqueda.trim() + "%");
            }
            
            if (estadoMinimo != null) {
                // Filtrar estados iguales o mejores
                List<EstadoProducto> estadosValidos = new ArrayList<>();
                for (EstadoProducto estado : EstadoProducto.values()) {
                    if (estado.esMejorOIgualQue(estadoMinimo)) {
                        estadosValidos.add(estado);
                    }
                }
                query.setParameter("estados", estadosValidos);
            }
            
            if (precioMaximo != null) {
                query.setParameter("precioMaximo", precioMaximo);
            }
            
            return query.getResultList();
        } catch (Exception e) {
            throw new RepositorioException("Error al buscar productos", e);
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
    }
    
    @Override
    public List<ResumenProducto> getHistorialMes(int mes, int anio) throws RepositorioException {
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            // Crear fechas de inicio y fin del mes
            LocalDateTime inicio = LocalDateTime.of(anio, mes, 1, 0, 0);
            LocalDateTime fin = inicio.plusMonths(1);
            
            TypedQuery<Producto> query = em.createQuery(
                "SELECT p FROM Producto p WHERE p.fechaPublicacion >= :inicio AND p.fechaPublicacion < :fin " +
                "ORDER BY p.visualizaciones DESC", 
                Producto.class
            );
            query.setParameter("inicio", inicio);
            query.setParameter("fin", fin);
            
            List<Producto> productos = query.getResultList();
            
            // Convertir a ResumenProducto
            return productos.stream()
                .map(p -> new ResumenProducto(
                    p.getId(),
                    p.getTitulo(),
                    p.getPrecio(),
                    p.getFechaPublicacion(),
                    p.getCategoria().getNombre(),
                    p.getVisualizaciones()
                ))
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RepositorioException("Error al obtener historial del mes " + mes + "/" + anio, e);
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
    }
    
    @Override
    public List<Producto> getProductosPorFechas(LocalDateTime inicio, LocalDateTime fin) throws RepositorioException {
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            TypedQuery<Producto> query = em.createQuery(
                "SELECT p FROM Producto p WHERE p.fechaPublicacion >= :inicio AND p.fechaPublicacion <= :fin", 
                Producto.class
            );
            query.setParameter("inicio", inicio);
            query.setParameter("fin", fin);
            
            return query.getResultList();
        } catch (Exception e) {
            throw new RepositorioException("Error al recuperar productos por fechas", e);
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
    }
}