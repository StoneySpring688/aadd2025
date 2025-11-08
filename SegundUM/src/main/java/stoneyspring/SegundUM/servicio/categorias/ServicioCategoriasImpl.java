package stoneyspring.SegundUM.servicio.categorias;

import stoneyspring.SegundUM.servicio.ServicioException;
import stoneyspring.SegundUM.dominio.Categoria;
import stoneyspring.SegundUM.repositorio.FactoriaRepositorios;
import stoneyspring.SegundUM.repositorio.categorias.RepositorioCategorias;
import stoneyspring.SegundUM.repositorio.RepositorioException;
import stoneyspring.SegundUM.repositorio.EntidadNoEncontrada;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.List;

/**
 * Implementación del servicio de categorías.
 */
public class ServicioCategoriasImpl implements ServicioCategorias {

    private final RepositorioCategorias repositorioCategorias;

    public ServicioCategoriasImpl() {
        this.repositorioCategorias = FactoriaRepositorios.getRepositorio(Categoria.class);
    }

    @Override
    public void cargarJerarquia(String ruta) throws ServicioException {
        InputStream is = null;
        try {
            // 1) Intentar cargar como recurso del classpath (posible ruta relativa dentro de resources)
            ClassLoader cl = Thread.currentThread().getContextClassLoader();

            // Si ruta apunta directamente a resources subfolder, respétala,
            // si no, intentamos prefijar "categoriasXML/"
            is = cl.getResourceAsStream(ruta);
            if (is == null) {
                is = cl.getResourceAsStream("categoriasXML/" + ruta);
            }
            // 2) Si no se encuentra en classpath, probar como fichero del sistema de archivos
            if (is == null) {
                File f = new File(ruta);
                if (f.exists()) {
                    is = new FileInputStream(f);
                } else {
                    // intentar con ruta relativa al proyecto
                    f = new File("src/main/resources/categoriasXML/" + ruta);
                    if (f.exists()) {
                        is = new FileInputStream(f);
                    }
                }
            }

            if (is == null) {
                throw new ServicioException("No se encontró el fichero XML en classpath ni en disco: " + ruta);
            }

            // Unmarshall con JAXB
            JAXBContext jaxb = JAXBContext.newInstance(Categoria.class);
            Unmarshaller u = jaxb.createUnmarshaller();

            Object obj = u.unmarshal(is);
            if (!(obj instanceof Categoria)) {
                throw new ServicioException("El fichero XML no contiene una categoría raíz válida: " + ruta);
            }
            Categoria raiz = (Categoria) obj;

            // Comprobamos si la categoría principal ya existe
            boolean existe = repositorioCategorias.existe(raiz.getId());
            if (existe) {
                // No cargarla según el requisito
                return;
            }

            // Guardar la jerarquía en cascada
            repositorioCategorias.add(raiz);

        } catch (RepositorioException e) {
            throw new ServicioException("Error persistiendo la jerarquía de categorías", e);
        } catch (Exception e) {
            throw new ServicioException("Error leyendo el fichero XML: " + ruta, e);
        } finally {
            if (is != null) {
                try { is.close(); } catch (IOException ignored) {}
            }
        }
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