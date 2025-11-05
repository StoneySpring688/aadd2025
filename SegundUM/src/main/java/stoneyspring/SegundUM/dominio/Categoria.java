package stoneyspring.SegundUM.dominio;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categorias")
@XmlRootElement(name = "categoria")
@XmlAccessorType(XmlAccessType.FIELD)
public class Categoria {
    
    @Id
    @XmlAttribute
    private String id;
    
    @Column(nullable = false)
    @XmlElement
    private String nombre;
    
    @Column(length = 1000)
    @XmlElement
    private String descripcion;
    
    @XmlTransient
    private String ruta;
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_padre_id")
    @XmlElement(name = "subcategoria")
    @XmlElementWrapper(name = "subcategorias")
    private List<Categoria> subcategorias = new ArrayList<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_padre_id", insertable = false, updatable = false)
    @XmlTransient
    private Categoria categoriaPadre;
    
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @XmlTransient
    private List<Producto> productos = new ArrayList<>();
    
    // Constructor por defecto para JPA y JAXB
    protected Categoria() {}
    
    public Categoria(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
    
    // Método para añadir subcategoría
    public void addSubcategoria(Categoria subcategoria) {
        subcategorias.add(subcategoria);
        subcategoria.setCategoriaPadre(this);
    }
    
    // Método para obtener todas las categorías descendientes (recursivo)
    public List<Categoria> obtenerDescendientes() {
        List<Categoria> descendientes = new ArrayList<>();
        for (Categoria subcategoria : subcategorias) {
            descendientes.add(subcategoria);
            descendientes.addAll(subcategoria.obtenerDescendientes());
        }
        return descendientes;
    }
    
    // Método para verificar si es raíz
    public boolean esRaiz() {
        return categoriaPadre == null;
    }
    
    // Getters y setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getRuta() {
        return ruta;
    }
    
    public void setRuta(String ruta) {
        this.ruta = ruta;
    }
    
    public List<Categoria> getSubcategorias() {
        return subcategorias;
    }
    
    public void setSubcategorias(List<Categoria> subcategorias) {
        this.subcategorias = subcategorias;
    }
    
    public Categoria getCategoriaPadre() {
        return categoriaPadre;
    }
    
    public void setCategoriaPadre(Categoria categoriaPadre) {
        this.categoriaPadre = categoriaPadre;
    }
    
    public List<Producto> getProductos() {
        return productos;
    }
}