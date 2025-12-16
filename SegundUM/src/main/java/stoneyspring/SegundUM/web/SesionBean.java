package stoneyspring.SegundUM.web;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import stoneyspring.SegundUM.dominio.Usuario;

@ManagedBean(name = "sesionBean")
@SessionScoped
public class SesionBean implements Serializable {

    private Usuario usuarioLogueado;

    public boolean estaLogueado() {
        return usuarioLogueado != null;
    }

    public String cerrarSesion() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/session/login?faces-redirect=true";
    }

    public Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public void setUsuarioLogueado(Usuario usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
    }
    
    public boolean esAdmin() {
        return usuarioLogueado != null && usuarioLogueado.isAdministrador();
    }
}