package stoneyspring.SegundUM.servicio.usuarios;

import java.time.LocalDate;
import stoneyspring.SegundUM.dominio.Usuario;
import stoneyspring.SegundUM.servicio.ServicioException;

/**
 * Operaciones de negocio sobre usuarios.
 */
public interface ServicioUsuarios {

    /**
     * Da de alta un usuario y devuelve su identificador.
     * Todos los usuarios se crean con administrador = false.
     */
    String altaUsuario(String email, String nombre, String apellidos, String clave,
                       LocalDate fechaNacimiento, String telefono) throws ServicioException;

    /**
     * Modifica los datos de un usuario existente.
     * Los parámetros pueden ser null si no se quieren modificar.
     */
    void modificarUsuario(String usuarioId, String nombre, String apellidos, String clave,
                         LocalDate fechaNacimiento, String telefono) throws ServicioException;
}