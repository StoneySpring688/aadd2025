package stoneyspring.SegundUM.dominio;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
public class Producto {
    
    @Id
    private String id;
    
    @Column(nullable = false)
    private String titulo;
    
    @Column(length = 2000)
    private String descripcion;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoProducto estado;
    
    @Column(name = "fecha_publicacion", nullable = false)
    private LocalDateTime fechaPublicacion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;
    
    @Column(nullable = false)
    private Integer visualizaciones;
    
    @Column(name = "envio_disponible", nullable = false)
    private boolean envioDisponible;
    
    @Embedded
    private LugarRecogida recogida;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendedor_id", nullable = false)
    private Usuario vendedor;
    
    // Constructor por defecto para JPA
    protected Producto() {}
    
    public Producto(String id, String titulo, String descripcion, BigDecimal precio,
                    EstadoProducto estado, Categoria categoria, boolean envioDisponible,
                    Usuario vendedor) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.estado = estado;
        this.categoria = categoria;
        this.envioDisponible = envioDisponible;
        this.vendedor = vendedor;
        this.fechaPublicacion = LocalDateTime.now();
        this.visualizaciones = 0;
    }
    
    // Método para incrementar visualizaciones
    public void incrementarVisualizaciones() {
        this.visualizaciones++;
    }
    
    // Getters y setters
    public String getId() {
        return id;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public BigDecimal getPrecio() {
        return precio;
    }
    
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
    
    public EstadoProducto getEstado() {
        return estado;
    }
    
    public void setEstado(EstadoProducto estado) {
        this.estado = estado;
    }
    
    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }
    
    public Categoria getCategoria() {
        return categoria;
    }
    
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
    
    public Integer getVisualizaciones() {
        return visualizaciones;
    }
    
    public boolean isEnvioDisponible() {
        return envioDisponible;
    }
    
    public void setEnvioDisponible(boolean envioDisponible) {
        this.envioDisponible = envioDisponible;
    }
    
    public LugarRecogida getRecogida() {
        return recogida;
    }
    
    public void setRecogida(LugarRecogida recogida) {
        this.recogida = recogida;
    }
    
    public Usuario getVendedor() {
        return vendedor;
    }
}