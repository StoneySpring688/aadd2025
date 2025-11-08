package stoneyspring.SegundUM.repositorio.usuarios;

import stoneyspring.SegundUM.dominio.Usuario;
import stoneyspring.SegundUM.repositorio.EntidadNoEncontrada;
import stoneyspring.SegundUM.repositorio.RepositorioException;
import stoneyspring.SegundUM.repositorio.RepositorioString;

/**
 * Repositorio específico para Usuarios con operaciones AdHoc.
 */
public interface RepositorioUsuarios extends RepositorioString<Usuario> {
    
    /**
     * Busca un usuario por su email.
     */
    Usuario getByEmail(String email) throws RepositorioException, EntidadNoEncontrada;
    
    /**
     * Verifica si existe un usuario con el email dado.
     */
    boolean existeEmail(String email) throws RepositorioException;
}