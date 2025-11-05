package stoneyspring.SegundUM.repositorio.AdHoc;

import java.util.List;

import stoneyspring.SegundUM.dominio.Categoria;
import stoneyspring.SegundUM.repositorio.EntidadNoEncontrada;
import stoneyspring.SegundUM.repositorio.RepositorioException;
import stoneyspring.SegundUM.repositorio.RepositorioString;

/**
 * Repositorio específico para Categorías con operaciones AdHoc.
 */
public interface RepositorioCategorias extends RepositorioString<Categoria> {
    
    /**
     * Recupera todas las categorías raíz (sin padre).
     */
    List<Categoria> getCategoriasRaiz() throws RepositorioException;
    
    /**
     * Recupera todos los descendientes de una categoría.
     */
    List<Categoria> getDescendientes(String categoriaId) throws RepositorioException, EntidadNoEncontrada;
    
    /**
     * Verifica si existe una categoría con el ID dado.
     */
    boolean existe(String id) throws RepositorioException;
}