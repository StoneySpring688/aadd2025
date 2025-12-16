package stoneyspring.SegundUM.web;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
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
@SessionScoped
public class ProductoBean implements Serializable {

    private String titulo;
    private String descripcion;
    private BigDecimal precio;
    private EstadoProducto estado;
    private String categoriaIdSeleccionada;
    private boolean envioDisponible;
    private String recogidaDescripcion;

    private List<Categoria> listaCategorias;
    private List<Producto> misProductos;

    @ManagedProperty("#{sesionBean}")
    private SesionBean sesionBean;

    private ServicioProductos servicioProductos;
    private ServicioCategorias servicioCategorias;
    private Producto productoSeleccionado;
    
    // variables para la busqueda
    private String busquedaTexto;
    private String busquedaCategoriaId;
    private EstadoProducto busquedaEstado;
    private BigDecimal busquedaPrecioMax;
    
    private List<Producto> productosEncontrados;

    public ProductoBean() {
        this.servicioProductos = FactoriaServicios.getServicio(ServicioProductos.class);
        this.servicioCategorias = FactoriaServicios.getServicio(ServicioCategorias.class);
    }

    @PostConstruct
    public void init() {
        try {
            this.listaCategorias = servicioCategorias.getCategoriasRaiz();
        } catch (ServicioException e) {
        				FacesContext.getCurrentInstance().addMessage(null, 
				new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudieron cargar las categorías"));
        }

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

            // producto básico
            String nuevoId = servicioProductos.altaProducto(
                titulo, 
                descripcion, 
                precio, 
                estado, 
                categoriaIdSeleccionada, 
                envioDisponible, 
                vendedorId
            );

            // Si el usuario escribió un lugar, asignarlo
            if (recogidaDescripcion != null && !recogidaDescripcion.trim().isEmpty()) {
                servicioProductos.asignarLugarRecogida(nuevoId, recogidaDescripcion, null, null); 
            }

            this.misProductos = servicioProductos.getProductosPorVendedor(vendedorId); 
            
            this.titulo = "";
            this.descripcion = "";
            this.precio = null;
            this.recogidaDescripcion = "";
            
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
    
    public void guardarEdicion() {
        if (productoSeleccionado == null) return;

        try {
            String idUsuario = sesionBean.getUsuarioLogueado().getId();

            servicioProductos.modificarProducto(
                productoSeleccionado.getId(),
                productoSeleccionado.getDescripcion(),
                productoSeleccionado.getPrecio(),
                idUsuario
            );

            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Producto actualizado."));
            
            this.misProductos = servicioProductos.getProductosPorVendedor(idUsuario);

        } catch (ServicioException e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }
    
    public String verDetalle(Producto producto) {
        try {
            // Guardamos el producto en el bean para mostrarlo en la siguiente vista
            this.productoSeleccionado = producto;

            // REQUISITO: Incrementar visualización
            servicioProductos.anadirVisualizacion(producto.getId());
            
            // Actualizamos el contador en el objeto local para que se vea el +1 inmediatamente en la vista
            if (this.productoSeleccionado.getVisualizaciones() != null) {
                this.productoSeleccionado.setVisualizaciones(this.productoSeleccionado.getVisualizaciones() + 1);
            }

            // Navegamos a la vista de detalle
            return "/productos/verProducto"; // Sin redirect para mantener el objeto productoSeleccionado en el ViewScope

        } catch (ServicioException e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo cargar el producto."));
            return null;
        }
    }
    
    // ojo la busqueda por estado, devuelve ese estado o mejor, se puede alterar deesde El repositorioJPA de productos, llamando a un método distinto de esMejorOIgualQue durante el proceso de busqueda :P
    public void buscar() {
        try {
            this.productosEncontrados = servicioProductos.buscarProductos(
                busquedaCategoriaId, 
                busquedaTexto, 
                busquedaEstado, 
                busquedaPrecioMax
            );
            
            if (this.productosEncontrados.isEmpty()) {
                 FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "No se encontraron productos con esos criterios."));
            }

        } catch (ServicioException e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al realizar la búsqueda: " + e.getMessage()));
            this.productosEncontrados = new ArrayList<>();
        }
    }
    
    // getters y setters para la busqueda
    public String getBusquedaTexto() { return busquedaTexto; }
    public void setBusquedaTexto(String busquedaTexto) { this.busquedaTexto = busquedaTexto; }

    public String getBusquedaCategoriaId() { return busquedaCategoriaId; }
    public void setBusquedaCategoriaId(String busquedaCategoriaId) { this.busquedaCategoriaId = busquedaCategoriaId; }

    public EstadoProducto getBusquedaEstado() { return busquedaEstado; }
    public void setBusquedaEstado(EstadoProducto busquedaEstado) { this.busquedaEstado = busquedaEstado; }

    public BigDecimal getBusquedaPrecioMax() { return busquedaPrecioMax; }
    public void setBusquedaPrecioMax(BigDecimal busquedaPrecioMax) { this.busquedaPrecioMax = busquedaPrecioMax; }

    public List<Producto> getProductosEncontrados() { return productosEncontrados; }
    
    // Getters y Setters
    public String getRecogidaDescripcion() { return recogidaDescripcion; }
    public void setRecogidaDescripcion(String recogidaDescripcion) { this.recogidaDescripcion = recogidaDescripcion; }
    
    public List<Producto> getMisProductos() { return misProductos; }

    public EstadoProducto[] getEstadosPosibles() { return EstadoProducto.values(); }
    
    public Producto getProductoSeleccionado() { return productoSeleccionado; }
    public void setProductoSeleccionado(Producto productoSeleccionado) { this.productoSeleccionado = productoSeleccionado; }

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