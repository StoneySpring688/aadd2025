package stoneyspring.SegundUM.repositorio.categorias;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import stoneyspring.SegundUM.dominio.Categoria;
import stoneyspring.SegundUM.repositorio.RepositorioException;
import stoneyspring.SegundUM.repositorio.RepositorioXML;

public class RepositorioCategoriasXML extends RepositorioXML<Categoria> {

	@Override
	public Class<Categoria> getClase() {
		return Categoria.class;
	}

	public Categoria deserializarDesdeArchivo(File archivo) throws RepositorioException {
        try {
            JAXBContext contexto = JAXBContext.newInstance(Categoria.class);
            Unmarshaller unmarshaller = contexto.createUnmarshaller();
            return (Categoria) unmarshaller.unmarshal(archivo);
        } catch (Exception e) {
            throw new RepositorioException("Error al deserializar la categoría desde el archivo: " + archivo.getPath(), e);
        }
    }
	
}
