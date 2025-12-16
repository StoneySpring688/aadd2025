package stoneyspring.SegundUM.web;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import stoneyspring.SegundUM.dominio.Categoria;
import stoneyspring.SegundUM.dominio.EstadoProducto;
import stoneyspring.SegundUM.dominio.Producto;
import stoneyspring.SegundUM.servicio.FactoriaServicios;
import stoneyspring.SegundUM.servicio.ServicioException;
import stoneyspring.SegundUM.servicio.categorias.ServicioCategorias;
import stoneyspring.SegundUM.servicio.productos.ServicioProductos;

@ManagedBean
@ViewScoped
public class ProductoBean implements Serializable {

    // Coinciden con los argumentos de altaProducto
    private String titulo;
    private String descripcion;
    private BigDecimal precio;
    private EstadoProducto estado;
    private String categoriaIdSeleccionada;
    private boolean envioDisponible; // Nuevo campo requerido por tu firma

    private List<Categoria> listaCategorias;
    private List<Producto> misProductos;

    @ManagedProperty("#{sesionBean}")
    private SesionBean sesionBean;

    private ServicioProductos servicioProductos;
    private ServicioCategorias servicioCategorias;

    public ProductoBean() {
        this.servicioProductos = FactoriaServicios.getServicio(ServicioProductos.class);
        this.servicioCategorias = FactoriaServicios.getServicio(ServicioCategorias.class);
    }

    @PostConstruct
    public void init() {
        // 1. Cargar categorías (código existente)
        try {
            this.listaCategorias = servicioCategorias.getCategoriasRaiz();
        } catch (ServicioException e) {
            // Manejo de error
        }

        // 2. NUEVO: Cargar mis productos
        try {
            if (sesionBean != null && sesionBean.getUsuarioLogueado() != null) {
                String idUsuario = sesionBean.getUsuarioLogueado().getId();
                this.misProductos = servicioProductos.getProductosPorVendedor(idUsuario);
            }
        } catch (ServicioException e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudieron cargar tus productos"));
        }
    }

    public String crearProducto() {
        try {
            String vendedorId = sesionBean.getUsuarioLogueado().getId();

            // FIRMA EXACTA QUE ME HAS DADO:
            // altaProducto(titulo, descripcion, precio, estado, categoriaId, envioDisponible, vendedorId)
            servicioProductos.altaProducto(
                titulo, 
                descripcion, 
                precio, 
                estado, 
                categoriaIdSeleccionada, 
                envioDisponible, 
                vendedorId
            );

            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Producto publicado correctamente."));

            return "/index?faces-redirect=true";

        } catch (ServicioException e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo crear: " + e.getMessage()));
            return null;
        }
    }
    
    public List<Producto> getMisProductos() {
        return misProductos;
    }

    public EstadoProducto[] getEstadosPosibles() {
        return EstadoProducto.values();
    }

    // Getters y Setters
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    
    public EstadoProducto getEstado() { return estado; }
    public void setEstado(EstadoProducto estado) { this.estado = estado; }
    
    public String getCategoriaIdSeleccionada() { return categoriaIdSeleccionada; }
    public void setCategoriaIdSeleccionada(String categoriaIdSeleccionada) { this.categoriaIdSeleccionada = categoriaIdSeleccionada; }
    
    public boolean isEnvioDisponible() { return envioDisponible; }
    public void setEnvioDisponible(boolean envioDisponible) { this.envioDisponible = envioDisponible; }
    
    public List<Categoria> getListaCategorias() { return listaCategorias; }
    
    public void setSesionBean(SesionBean sesionBean) { this.sesionBean = sesionBean; }
}