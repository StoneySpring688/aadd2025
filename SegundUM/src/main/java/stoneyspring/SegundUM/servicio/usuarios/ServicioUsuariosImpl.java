package stoneyspring.SegundUM.servicio.usuarios;

import stoneyspring.SegundUM.servicio.ServicioException;
import stoneyspring.SegundUM.dominio.Usuario;
import stoneyspring.SegundUM.repositorio.FactoriaRepositorios;
import stoneyspring.SegundUM.repositorio.usuarios.RepositorioUsuarios;
import stoneyspring.SegundUM.repositorio.RepositorioException;
import stoneyspring.SegundUM.repositorio.EntidadNoEncontrada;

import java.time.LocalDate;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServicioUsuariosImpl implements ServicioUsuarios {
	private final Logger logger = LoggerFactory.getLogger(ServicioUsuariosImpl.class);

    private final RepositorioUsuarios repositorioUsuarios;

    public ServicioUsuariosImpl() {
        this.repositorioUsuarios = FactoriaRepositorios.getRepositorio(Usuario.class);
    }

    @Override
    public String altaUsuario(String email, String nombre, String apellidos, String clave,
                              LocalDate fechaNacimiento, String telefono) throws ServicioException {
        try {
            // VERIFICACIÓN: Comprobar que el email no existe ya
            if (repositorioUsuarios.existeEmail(email)) {
            	logger.warn("Intento de alta con email ya registrado: " + email);
                throw new ServicioException("El email " + email + " ya está registrado en el sistema");
            }

            // Generar id único
            String id = UUID.randomUUID().toString();

            Usuario u = new Usuario(id, email, nombre, apellidos, clave, fechaNacimiento, telefono);
            // administrador por defecto ya false en el constructor de dominio

            logger.debug("Dando de alta nuevo usuario: " + u.toString());
            return repositorioUsuarios.add(u);
        } catch (RepositorioException e) {
        	logger.error("Error al dar de alta el usuario con email: " + email, e);
            throw new ServicioException("Error al dar de alta el usuario", e);
        }
    }

    @Override
    public void modificarUsuario(String usuarioId, String nombre, String apellidos, String clave,
                                 LocalDate fechaNacimiento, String telefono) throws ServicioException {
        try {
            Usuario u = repositorioUsuarios.getById(usuarioId);

            if (nombre != null) u.setNombre(nombre);
            if (apellidos != null) u.setApellidos(apellidos);
            if (clave != null) u.setClave(clave);
            if (fechaNacimiento != null) u.setFechaNacimiento(fechaNacimiento);
            if (telefono != null) u.setTelefono(telefono);

            repositorioUsuarios.update(u);
        } catch (EntidadNoEncontrada e) {
            // VERIFICACIÓN: Mensaje claro cuando el usuario no existe
        	logger.error("Intento de modificación de usuario inexistente con ID: " + usuarioId, e);
            throw new ServicioException("El usuario con ID " + usuarioId + " no existe en el sistema", e);
        } catch (RepositorioException e) {
        	logger.error("Error al modificar el usuario con ID: " + usuarioId, e);
            throw new ServicioException("Error al modificar usuario " + usuarioId, e);
        }
    }
}