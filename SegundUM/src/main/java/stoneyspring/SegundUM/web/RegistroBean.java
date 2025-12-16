package stoneyspring.SegundUM.web;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import stoneyspring.SegundUM.servicio.FactoriaServicios;
import stoneyspring.SegundUM.servicio.ServicioException;
import stoneyspring.SegundUM.servicio.usuarios.ServicioUsuarios;

@ManagedBean
@ViewScoped
public class RegistroBean implements Serializable {

    private String nombre;
    private String apellidos;
    private String email;
    private String clave;
    private String telefono;
    private Date fechaNacimientoDate;

    private ServicioUsuarios servicioUsuarios;

    public RegistroBean() {
        this.servicioUsuarios = FactoriaServicios.getServicio(ServicioUsuarios.class);
    }

    public String registrar() {
        try {
            LocalDate fechaNacimiento = null;
            if (fechaNacimientoDate != null) {
                fechaNacimiento = fechaNacimientoDate.toInstant()
                                  .atZone(ZoneId.systemDefault())
                                  .toLocalDate();
            }

            servicioUsuarios.altaUsuario(email, nombre, apellidos, clave, fechaNacimiento, telefono);
            
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Usuario registrado correctamente. Por favor inicie sesión."));
            
            return "/session/login?faces-redirect=true";

        } catch (ServicioException e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error de registro", e.getMessage()));
            return null;
        }
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public Date getFechaNacimientoDate() { return fechaNacimientoDate; }
    public void setFechaNacimientoDate(Date fechaNacimientoDate) { this.fechaNacimientoDate = fechaNacimientoDate; }
}