package stoneyspring.SegundUM.web;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import stoneyspring.SegundUM.dominio.Usuario;
import stoneyspring.SegundUM.servicio.FactoriaServicios;
import stoneyspring.SegundUM.servicio.ServicioException;
import stoneyspring.SegundUM.servicio.usuarios.ServicioUsuarios;

@ManagedBean
@RequestScoped
public class LoginBean {

    private String email;
    private String clave;

    // Inyectamos el bean de sesión para guardar al usuario si el login es exitoso
    @ManagedProperty("#{sesionBean}")
    private SesionBean sesionBean;

    private ServicioUsuarios servicioUsuarios;

    public LoginBean() {
        // Obtenemos el servicio usando tu Factoría
        this.servicioUsuarios = FactoriaServicios.getServicio(ServicioUsuarios.class);
    }

    public String entrar() {
        try {
            Usuario usuario = servicioUsuarios.login(email, clave);
            
            // Login correcto: guardamos en sesión
            sesionBean.setUsuarioLogueado(usuario);
            
            // Navegación (asumiendo que tienes una pagina 'index' o 'home')
            return "/index?faces-redirect=true";
            
        } catch (ServicioException e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error de acceso", e.getMessage()));
            return null;
        }
    }

    // Getters y Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }
    public void setSesionBean(SesionBean sesionBean) { this.sesionBean = sesionBean; }
}