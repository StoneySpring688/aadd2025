package stoneyspring.SegundUM.repositorio.usuarios;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import stoneyspring.SegundUM.dominio.Usuario;
import stoneyspring.SegundUM.repositorio.EntidadNoEncontrada;
import stoneyspring.SegundUM.repositorio.RepositorioException;
import stoneyspring.SegundUM.repositorio.RepositorioJPA;
import stoneyspring.SegundUM.utils.EntityManagerHelper;

/**
 * Implementación JPA del repositorio de usuarios.
 */
public class RepositorioUsuariosJPA extends RepositorioJPA<Usuario> implements RepositorioUsuarios {
    
    @Override
    public Class<Usuario> getClase() {
        return Usuario.class;
    }
    
    @Override
    public Usuario getByEmail(String email) throws RepositorioException, EntidadNoEncontrada {
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            TypedQuery<Usuario> query = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.email = :email", 
                Usuario.class
            );
            query.setParameter("email", email);
            
            List<Usuario> usuarios = query.getResultList();
            
            if (usuarios.isEmpty()) {
                throw new EntidadNoEncontrada("Usuario con email " + email + " no encontrado");
            }
            
            return usuarios.get(0);
        } catch (EntidadNoEncontrada e) {
            throw e;
        } catch (Exception e) {
            throw new RepositorioException("Error al buscar usuario por email " + email, e);
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
    }
    
    @Override
    public boolean existeEmail(String email) throws RepositorioException {
        EntityManager em = EntityManagerHelper.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(u) FROM Usuario u WHERE u.email = :email", 
                Long.class
            );
            query.setParameter("email", email);
            
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            throw new RepositorioException("Error al verificar existencia de email " + email, e);
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
    }
}