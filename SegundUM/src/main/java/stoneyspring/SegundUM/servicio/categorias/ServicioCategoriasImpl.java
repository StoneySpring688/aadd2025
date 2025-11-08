package stoneyspring.SegundUM.servicio.categorias;

import stoneyspring.SegundUM.servicio.ServicioException;
import stoneyspring.SegundUM.dominio.Categoria;
import stoneyspring.SegundUM.repositorio.FactoriaRepositorios;
import stoneyspring.SegundUM.repositorio.categorias.RepositorioCategorias;
import stoneyspring.SegundUM.repositorio.categorias.RepositorioCategoriasXML;
import stoneyspring.SegundUM.repositorio.RepositorioException;
import stoneyspring.SegundUM.repositorio.EntidadNoEncontrada;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Implementación del servicio de categorías.
 */
public class ServicioCategoriasImpl implements ServicioCategorias {
	private static final Logger logger = LoggerFactory.getLogger(ServicioCategoriasImpl.class);

    private final RepositorioCategorias repositorioCategorias;
    private final RepositorioCategoriasXML repositorioCategoriasXML;

    public ServicioCategoriasImpl() {
        this.repositorioCategorias = FactoriaRepositorios.getRepositorio(Categoria.class);
        this.repositorioCategoriasXML = new RepositorioCategoriasXML();
    }

    @Override
    public void cargarJerarquia(String ruta) throws ServicioException {
        try {
            Categoria raiz = repositorioCategoriasXML.getById(ruta);
            if (!repositorioCategorias.existe(raiz.getId())) {
                repositorioCategorias.add(raiz);
                logger.info("Jerarquía de categorías cargada: " + raiz.toString());
            } else {
                logger.info("La categoría " + raiz.getNombre() + " ya existe. No se cargará.");
            }

        } catch (RepositorioException e) {
        	logger.error("Error accediendo al repositorio de categorías", e);
            throw new ServicioException("Error accediendo al repositorio de categorías", e);
        } catch (Exception e) {
        	logger.error("Error al cargar la jerarquía desde el XML: " + ruta, e);
            throw new ServicioException("Error al cargar la jerarquía desde el XML: " + ruta, e);
        }
    }

    @Override
    public void modificarDescripcion(String categoriaId, String nuevaDescripcion) throws ServicioException {
        try {
            Categoria c = repositorioCategorias.getById(categoriaId);
            c.setDescripcion(nuevaDescripcion);
            repositorioCategorias.update(c);
        } catch (EntidadNoEncontrada e) {
            throw new ServicioException("La categoría " + categoriaId + " no existe", e);
        } catch (RepositorioException e) {
            throw new ServicioException("Error al modificar la descripción de la categoría " + categoriaId, e);
        }
    }

    @Override
    public List<Categoria> getCategoriasRaiz() throws ServicioException {
        try {
            return repositorioCategorias.getCategoriasRaiz();
        } catch (RepositorioException e) {
            throw new ServicioException("Error al recuperar categorías raíz", e);
        }
    }

    @Override
    public List<Categoria> getDescendientes(String categoriaId) throws ServicioException {
        try {
            return repositorioCategorias.getDescendientes(categoriaId);
        } catch (EntidadNoEncontrada e) {
            throw new ServicioException("La categoría " + categoriaId + " no existe", e);
        } catch (RepositorioException e) {
            throw new ServicioException("Error al recuperar descendientes de " + categoriaId, e);
        }
    }
}