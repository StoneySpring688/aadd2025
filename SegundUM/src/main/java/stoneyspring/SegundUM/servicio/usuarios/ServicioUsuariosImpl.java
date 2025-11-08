package stoneyspring.SegundUM.servicio.usuarios;

import stoneyspring.SegundUM.servicio.ServicioException;
import stoneyspring.SegundUM.dominio.Usuario;
import stoneyspring.SegundUM.repositorio.FactoriaRepositorios;
import stoneyspring.SegundUM.repositorio.usuarios.RepositorioUsuarios;
import stoneyspring.SegundUM.repositorio.RepositorioException;
import stoneyspring.SegundUM.repositorio.EntidadNoEncontrada;

import java.time.LocalDate;
import java.util.UUID;

public class ServicioUsuariosImpl implements ServicioUsuarios {

    private final RepositorioUsuarios repositorioUsuarios;

    public ServicioUsuariosImpl() {
        this.repositorioUsuarios = FactoriaRepositorios.getRepositorio(Usuario.class);
    }

    @Override
    public String altaUsuario(String email, String nombre, String apellidos, String clave,
                              LocalDate fechaNacimiento, String telefono) throws ServicioException {
        try {
            // Generar id único
            String id = UUID.randomUUID().toString();

            Usuario u = new Usuario(id, email, nombre, apellidos, clave, fechaNacimiento, telefono);
            // administrador por defecto ya false en el constructor de dominio

            return repositorioUsuarios.add(u);
        } catch (RepositorioException e) {
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
            throw new ServicioException("Usuario no encontrado: " + usuarioId, e);
        } catch (RepositorioException e) {
            throw new ServicioException("Error al modificar usuario " + usuarioId, e);
        }
    }
}