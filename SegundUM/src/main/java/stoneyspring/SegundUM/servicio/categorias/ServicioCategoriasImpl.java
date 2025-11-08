package stoneyspring.SegundUM.servicio.categorias;

import stoneyspring.SegundUM.servicio.ServicioException;
import stoneyspring.SegundUM.dominio.Categoria;
import stoneyspring.SegundUM.repositorio.FactoriaRepositorios;
import stoneyspring.SegundUM.repositorio.categorias.RepositorioCategorias;
import stoneyspring.SegundUM.repositorio.categorias.RepositorioCategoriasXML;
import stoneyspring.SegundUM.repositorio.RepositorioException;
import stoneyspring.SegundUM.repositorio.EntidadNoEncontrada;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

/**
 * Implementación del servicio de categorías.
 */
public class ServicioCategoriasImpl implements ServicioCategorias {
	private static final Logger logger = LoggerFactory.getLogger(ServicioCategoriasImpl.class);

    private final RepositorioCategorias repositorioCategorias;
    private final RepositorioCategoriasXML repositorioCategoriasXML;

    public ServicioCategoriasImpl() {
        this.repositorioCategorias = FactoriaRepositorios.getRepositorio(Categoria.class);
        this.repositorioCategoriasXML = new RepositorioCategoriasXML();
    }

    @Override
    public void cargarJerarquia(String ruta) throws ServicioException {
        try {
            // 1. Usar RepositorioCategoriasXML para leer el fichero XML
            // El RepositorioXML ya maneja la deserialización con JAXB
            
            // Primero, necesitamos cargar el XML usando el repositorioCategoriasXML
            // Guardamos temporalmente el XML para poder deserializarlo
            File archivoXML = resolverRutaArchivo(ruta);
            
            if (archivoXML == null || !archivoXML.exists()) {
                throw new ServicioException("No se encontró el fichero XML: " + ruta);
            }

            // Deserializar usando JAXB (como hace el RepositorioXML internamente)
            Categoria raiz = repositorioCategoriasXML.deserializarDesdeArchivo(archivoXML);

            // 2. Comprobar si la categoría principal ya existe en la base de datos (JPA)
            boolean existe = repositorioCategorias.existe(raiz.getId());
            
            if (existe) {
                logger.debug("La categoría raíz con id " + raiz.getId() + " ya existe. No se cargará nuevamente.");
                return; // No cargar si ya existe
            }

            // 3. Usar RepositorioCategorias (JPA) para persistir toda la jerarquía
            // El cascade=ALL de la entidad Categoria persistirá automáticamente las subcategorías
            repositorioCategorias.add(raiz);
            logger.debug("Categoría raíz agregada: " + raiz.getNombre() + " (id: " + raiz.getId() + ")");

        } catch (RepositorioException e) {
            throw new ServicioException("Error accediendo al repositorio de categorías", e);
        } catch (Exception e) {
            throw new ServicioException("Error al cargar la jerarquía desde el XML: " + ruta, e);
        }
    }

    /**
     * Método auxiliar para resolver la ruta del archivo XML
     * Busca en classpath y en el sistema de archivos
     */
    private File resolverRutaArchivo(String ruta) {
        // 1. Intentar como recurso del classpath
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream is = cl.getResourceAsStream(ruta);
        
        if (is == null) {
            is = cl.getResourceAsStream("categoriasXML/" + ruta);
        }
        
        // Si se encuentra en classpath, crear archivo temporal
        if (is != null) {
            try {
                File temp = File.createTempFile("categoria", ".xml");
                temp.deleteOnExit();
                try (FileOutputStream out = new FileOutputStream(temp)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }
                is.close();
                return temp;
            } catch (IOException e) {
                logger.error("Error al crear archivo temporal desde classpath", e);
            }
        }

        // 2. Intentar como archivo del sistema
        File archivo = new File(ruta);
        if (archivo.exists()) {
            return archivo;
        }

        // 3. Intentar con ruta relativa al proyecto
        archivo = new File("src/main/resources/categoriasXML/" + ruta);
        if (archivo.exists()) {
            return archivo;
        }

        return null;
    }

    @Override
    public void modificarDescripcion(String categoriaId, String nuevaDescripcion) throws ServicioException {
        try {
            Categoria c = repositorioCategorias.getById(categoriaId);
            c.setDescripcion(nuevaDescripcion);
            repositorioCategorias.update(c);
        } catch (EntidadNoEncontrada e) {
            throw new ServicioException("La categoría " + categoriaId + " no existe", e);
        } catch (RepositorioException e) {
            throw new ServicioException("Error al modificar la descripción de la categoría " + categoriaId, e);
        }
    }

    @Override
    public List<Categoria> getCategoriasRaiz() throws ServicioException {
        try {
            return repositorioCategorias.getCategoriasRaiz();
        } catch (RepositorioException e) {
            throw new ServicioException("Error al recuperar categorías raíz", e);
        }
    }

    @Override
    public List<Categoria> getDescendientes(String categoriaId) throws ServicioException {
        try {
            return repositorioCategorias.getDescendientes(categoriaId);
        } catch (EntidadNoEncontrada e) {
            throw new ServicioException("La categoría " + categoriaId + " no existe", e);
        } catch (RepositorioException e) {
            throw new ServicioException("Error al recuperar descendientes de " + categoriaId, e);
        }
    }
}